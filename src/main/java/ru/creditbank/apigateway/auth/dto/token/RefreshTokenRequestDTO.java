package ru.creditbank.apigateway.auth.dto.token;

public record RefreshTokenRequestDTO(
        String refreshToken
) {}
