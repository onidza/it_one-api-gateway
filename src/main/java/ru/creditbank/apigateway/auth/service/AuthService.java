package ru.creditbank.apigateway.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.apigateway.auth.dto.login.LoginRequestDTO;
import ru.creditbank.apigateway.auth.dto.login.LoginResponseDTO;
import ru.creditbank.apigateway.auth.dto.register.RegisterRequestDTO;
import ru.creditbank.apigateway.auth.dto.session.AuthSessionData;
import ru.creditbank.apigateway.auth.dto.session.SessionAttribute;
import ru.creditbank.apigateway.auth.dto.token.TokenResponseDTO;
import ru.creditbank.apigateway.auth.entity.UserEntity;
import ru.creditbank.apigateway.auth.exception.UserAlreadyExistsException;
import ru.creditbank.apigateway.auth.repository.UserRepository;
import ru.creditbank.apigateway.config.property.JwtProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final JwtProperties jwtProperties;

  @Transactional(rollbackFor = Exception.class)
  public void register(RegisterRequestDTO request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new UserAlreadyExistsException(request.email());
    }

    String passwordHash = passwordEncoder.encode(request.password());
    userRepository.save(new UserEntity(
        request.email(),
        passwordHash,
        request.fullName().firstName(),
        request.fullName().lastName(),
        request.fullName().middleName()
    ));
    log.info("New user was registered = {}", request.email());
  }

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public LoginResponseDTO login(
      LoginRequestDTO request,
      HttpServletRequest httpRequest
  ) {
    log.info(
        "Try to login with email = {}",
        request.email()
    );

    authenticationManager.authenticate(
        UsernamePasswordAuthenticationToken.unauthenticated(
            request.email(),
            request.password()
        )
    );

    UserEntity user = userRepository.findByEmail(request.email())
        .orElseThrow(() ->
            new BadCredentialsException(
                "Invalid email or password"
            )
        );

    String accessToken =
        jwtService.generateAccessToken(
            user.getId(),
            user.getEmail(),
            user.getRole()
        );

    String refreshToken =
        jwtService.generateRefreshToken();

    AuthSessionData sessionData =
        new AuthSessionData(
            user.getId(),
            accessToken,
            refreshToken,
            Instant.now().plus(
                jwtProperties.refreshTokenTtl()
            )
        );

    HttpSession session = prepareSession(httpRequest);
    session.setAttribute(
        SessionAttribute.AUTH_SESSION,
        sessionData
    );

    log.info(
        "User successfully logged in, userId = {}, sessionId = {}",
        user.getId(),
        session.getId()
    );

    return new LoginResponseDTO(
        accessToken,
        refreshToken
    );
  }

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public TokenResponseDTO refresh(
      String providedRefreshToken,
      HttpServletRequest request
  ) {
    HttpSession session =
        requireSession(request);

    AuthSessionData sessionData =
        requireAuthSessionData(session);

    if (!Objects.equals(
        sessionData.refreshToken(),
        providedRefreshToken
    )) {
      throw new BadCredentialsException(
          "Invalid refresh token"
      );
    }

    if (!sessionData.refreshTokenExpiresAt()
        .isAfter(Instant.now())) {

      session.invalidate();

      throw new BadCredentialsException(
          "Refresh token expired"
      );
    }

    UserEntity user = userRepository.findById(sessionData.userId())
        .orElseThrow(() -> {
              session.invalidate();

              return new BadCredentialsException(
                  "User not found"
              );
            }
        );

    String newAccessToken =
        jwtService.generateAccessToken(
            user.getId(),
            user.getEmail(),
            user.getRole()
        );

    session.setAttribute(
        SessionAttribute.AUTH_SESSION,
        sessionData.withAccessToken(
            newAccessToken
        )
    );

    log.info(
        "New access token was generated, userId = {}",
        user.getId()
    );

    return new TokenResponseDTO(
        newAccessToken,
        sessionData.refreshToken()
    );
  }

  private HttpSession prepareSession(
      HttpServletRequest request
  ) {
    HttpSession session = request.getSession(false);

    if (session == null) {
      return request.getSession(true);
    }

    request.changeSessionId();

    return session;
  }

  private HttpSession requireSession(
      HttpServletRequest request
  ) {
    HttpSession session =
        request.getSession(false);

    if (session == null) {
      throw new BadCredentialsException(
          "Authentication session not found"
      );
    }

    return session;
  }

  private AuthSessionData requireAuthSessionData(
      HttpSession session
  ) {
    Object attribute = session.getAttribute(SessionAttribute.AUTH_SESSION);

    if (!(attribute instanceof AuthSessionData sessionData)) {
      session.invalidate();

      throw new BadCredentialsException(
          "Invalid authentication session"
      );
    }

    return sessionData;
  }
}
