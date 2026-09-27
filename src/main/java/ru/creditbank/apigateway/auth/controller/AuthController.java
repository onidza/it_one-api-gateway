package ru.creditbank.apigateway.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.apigateway.auth.dto.login.LoginRequestDTO;
import ru.creditbank.apigateway.auth.dto.login.LoginResponseDTO;
import ru.creditbank.apigateway.auth.dto.register.RegisterRequestDTO;
import ru.creditbank.apigateway.auth.dto.token.RefreshTokenRequestDTO;
import ru.creditbank.apigateway.auth.dto.token.TokenResponseDTO;
import ru.creditbank.apigateway.auth.service.AuthService;

/**
 * Main controller for authentication and authorization user flow.
 *
 * <p>Provides endpoints for register, login and refresh access Jwt token.
 * Firstly register, then login with same data.
 */
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

  @Operation(
      description = """
          There are test data account stored in application.yml with path: test.credit-manager"
          """
  )
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(
      @Valid @RequestBody LoginRequestDTO request,
      HttpServletRequest httpRequest
  ) {
    log.info("Received login request for email = {}", request.email());

    return ResponseEntity.ok(
        authService.login(request, httpRequest)
    );
  }

  @PostMapping("/refresh")
  public TokenResponseDTO refresh(
      @RequestBody @Valid RefreshTokenRequestDTO request,
      HttpServletRequest httpRequest
  ) {
    return authService.refresh(
        request.refreshToken(),
        httpRequest
    );
  }
}
