package ru.creditbank.apigateway.auth.dto.error;

public record ErrorResponseDTO(
        int status,
        String message
) {}