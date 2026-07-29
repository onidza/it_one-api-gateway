package ru.creditbank.apigateway.auth.dto;

public record ErrorResponseDTO(
        int status,
        String message
) {}
