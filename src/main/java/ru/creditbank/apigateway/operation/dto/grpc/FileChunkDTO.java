package ru.creditbank.apigateway.operation.dto.grpc;

public record FileChunkDTO(
    String fileName,
    String data,
    int chunkNumber,
    Long totalBytes
) {
}
