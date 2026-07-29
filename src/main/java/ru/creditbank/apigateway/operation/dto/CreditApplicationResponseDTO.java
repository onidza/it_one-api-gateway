package ru.creditbank.apigateway.operation.dto;

import java.time.Instant;
import java.util.UUID;

public record CreditApplicationResponseDTO(
    UUID id,
    Status status,
    Instant createdAt
) {}
