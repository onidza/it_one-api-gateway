package ru.creditbank.apigateway.operation.dto.grpc;

public record TransferProgressDTO(
    long bytesProcessed,
    long totalBytes,
    int percent
) {
}
