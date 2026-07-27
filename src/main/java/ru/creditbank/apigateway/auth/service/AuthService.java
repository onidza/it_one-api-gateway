package ru.creditbank.apigateway.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditbank.apigateway.auth.dto.LoginRequest;
import ru.creditbank.apigateway.auth.dto.LoginResponse;
import ru.creditbank.apigateway.auth.dto.RegisterRequest;
import ru.creditbank.apigateway.auth.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    public void register(RegisterRequest request) {
    }

    public LoginResponse login(LoginRequest request) {
    }
}
