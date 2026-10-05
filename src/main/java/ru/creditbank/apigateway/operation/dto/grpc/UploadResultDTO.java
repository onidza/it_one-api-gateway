package ru.creditbank.apigateway.operation.dto.grpc;

public record UploadResultDTO(
    String fileName,
    long totalBytes,
    int chunksCount
) {
}
