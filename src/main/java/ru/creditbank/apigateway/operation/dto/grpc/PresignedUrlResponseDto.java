package ru.creditbank.apigateway.operation.dto.grpc;

import java.time.Instant;

public record PresignedUrlResponseDto(
    String url,
    Instant expiresAt
) {

}
