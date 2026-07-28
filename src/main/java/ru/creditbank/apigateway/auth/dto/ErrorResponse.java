package ru.creditbank.apigateway.auth.dto;

public record ErrorResponse(
        int status,
        String message
) {}
