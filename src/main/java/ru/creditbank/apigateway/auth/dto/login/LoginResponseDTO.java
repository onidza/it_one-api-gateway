package ru.creditbank.apigateway.auth.dto.login;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken
) {}
