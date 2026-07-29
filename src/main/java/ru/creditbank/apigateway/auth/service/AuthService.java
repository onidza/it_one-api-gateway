package ru.creditbank.apigateway.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.apigateway.auth.dto.LoginRequestDTO;
import ru.creditbank.apigateway.auth.dto.LoginResponseDTO;
import ru.creditbank.apigateway.auth.dto.RegisterRequestDTO;
import ru.creditbank.apigateway.auth.dto.TokenResponseDTO;
import ru.creditbank.apigateway.auth.entity.UserEntity;
import ru.creditbank.apigateway.auth.exception.UserAlreadyExistsException;
import ru.creditbank.apigateway.auth.mapper.UserMapper;
import ru.creditbank.apigateway.auth.property.JwtProperties;
import ru.creditbank.apigateway.auth.repository.UserRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;

    @Transactional
    public void register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        String passwordHash = passwordEncoder.encode(request.password());
        UserEntity user = userMapper.toEntity(request, passwordHash);

        userRepository.save(user);
        log.info("New user was registered = {}", user.getId());
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Try to login with email = {}", request.email());

        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken
                        .unauthenticated(
                                request.email(),
                                request.password()
                        )
        );

        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid email or password"));

        Instant now = Instant.now();

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                user.getRole()
        );
        user.setAccessToken(accessToken);
        user.setAccessTokenIssuedAt(now);

        String refreshToken = jwtService.generateRefreshToken();
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiresAt(
                now.plus(jwtProperties.refreshTokenTtl())
        );

        log.info("User successfully logged in, email = {}", user.getEmail());

        return new LoginResponseDTO(
                accessToken,
                refreshToken
        );
    }

    @Transactional
    public TokenResponseDTO refresh(String refreshToken) {
        UserEntity user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid refresh token")
                );

        if (user.getRefreshTokenExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        String newAccessToken = jwtService.generateAccessToken(
                user.getEmail(),
                user.getRole()
        );

        user.setAccessToken(newAccessToken);
        user.setAccessTokenIssuedAt(Instant.now());
        log.info("New access token was set for user, email = {}", user.getEmail());

        return new TokenResponseDTO(
                newAccessToken,
                refreshToken
        );
    }
}
