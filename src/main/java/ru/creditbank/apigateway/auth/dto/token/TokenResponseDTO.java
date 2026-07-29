package ru.creditbank.apigateway.auth.dto.token;

public record TokenResponseDTO(
        String accessToken,
        String refreshToken
) {}
