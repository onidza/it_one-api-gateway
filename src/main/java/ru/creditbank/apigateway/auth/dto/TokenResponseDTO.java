package ru.creditbank.apigateway.auth.dto;

public record TokenResponseDTO(
        String accessToken,
        String refreshToken
) {}
