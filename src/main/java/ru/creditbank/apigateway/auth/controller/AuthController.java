package ru.creditbank.apigateway.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.apigateway.auth.dto.LoginRequestDTO;
import ru.creditbank.apigateway.auth.dto.LoginResponseDTO;
import ru.creditbank.apigateway.auth.dto.RegisterRequestDTO;
import ru.creditbank.apigateway.auth.service.AuthService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {
        log.info("Received registration request for email = {}", request.email());
        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request
    ) {
        log.info("Received login request for email = {}", request.email());

        return ResponseEntity.ok(authService.login(request));
    }
}
