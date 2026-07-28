package ru.creditbank.apigateway.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.apigateway.auth.dto.LoginRequestDTO;
import ru.creditbank.apigateway.auth.dto.LoginResponseDTO;
import ru.creditbank.apigateway.auth.dto.RegisterRequestDTO;
import ru.creditbank.apigateway.auth.entity.UserEntity;
import ru.creditbank.apigateway.auth.exception.UserAlreadyExistsException;
import ru.creditbank.apigateway.auth.mapper.UserMapper;
import ru.creditbank.apigateway.auth.repository.UserRepository;
import ru.creditbank.apigateway.jwt.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid email or password"));

        Authentication authentication =
                authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken
                                .unauthenticated(
                                        request.email(),
                                        request.password()
                                )
                );

        String token = jwtService.generateToken(authentication);
        log.info("User successfully logged in, id = {}", user.getId());

        return new LoginResponseDTO(token);
    }
}
