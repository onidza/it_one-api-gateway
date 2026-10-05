package ru.creditbank.apigateway.operation.controller.grpc;

import com.google.protobuf.Timestamp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.creditbank.apigateway.operation.dto.grpc.PresignedUrlResponseDto;
import ru.creditbank.contract.s3.v1.DownloadFileChunk;
import ru.creditbank.contract.s3.v1.DownloadFileRequest;
import ru.creditbank.contract.s3.v1.FileServiceGrpc;
import ru.creditbank.contract.s3.v1.GenerateUrlRequest;
import ru.creditbank.contract.s3.v1.GenerateUrlResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/grpc-test-s3")
@Slf4j
public class S3TestControllerGrpc {

  private final FileServiceGrpc.FileServiceBlockingStub fileServiceBlockingStub;

  @GetMapping("/files/{fileId}")
  public ResponseEntity<StreamingResponseBody> download(@PathVariable String fileId) {
    log.info("Received request for download file = {}", fileId);

    StreamingResponseBody body = outputStream -> {

      Iterator<DownloadFileChunk> iterator = fileServiceBlockingStub.downloadFile(
          DownloadFileRequest.newBuilder()
              .setFileId(fileId)
              .build()
      );

      log.info("After stub request");

      while (iterator.hasNext()) {
        DownloadFileChunk chunk = iterator.next();
        chunk.getData().writeTo(outputStream);
      }
    };

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment()
                .filename(fileId, StandardCharsets.UTF_8)
                .build()
                .toString()
        )
        .body(body);
  }

  @Operation(
      summary = "Generate presigned URL for file download",
      description = "Returns a temporary URL for downloading a file directly from S3"
  )
  @GetMapping("/files/{fileId}/presigned-url")
  public ResponseEntity<PresignedUrlResponseDto> getURLForDownload(@PathVariable String fileId) {
    GenerateUrlResponse response = fileServiceBlockingStub.generatePresignedUrl(
        GenerateUrlRequest.newBuilder().setFileId(fileId).build());

    return ResponseEntity.ok(new PresignedUrlResponseDto(
        response.getUrl(),
        mapTimestampToInstant(response.getExpiresAt())));
  }

  private Instant mapTimestampToInstant(Timestamp timestamp) {
    return Instant.ofEpochSecond(
        timestamp.getSeconds(),
        timestamp.getNanos()
    );
  }
}
