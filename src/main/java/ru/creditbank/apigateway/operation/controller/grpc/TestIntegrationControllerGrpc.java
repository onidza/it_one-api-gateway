package ru.creditbank.apigateway.operation.controller.grpc;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.creditbank.apigateway.operation.dto.grpc.DownloadRequestDTO;
import ru.creditbank.apigateway.operation.dto.grpc.FileChunkDTO;
import ru.creditbank.apigateway.operation.dto.grpc.TestRequestDTO;
import ru.creditbank.apigateway.operation.dto.grpc.TestResponseDTO;
import ru.creditbank.apigateway.operation.dto.grpc.TransferProgressDTO;
import ru.creditbank.apigateway.operation.dto.grpc.UploadResultDTO;
import ru.creditbank.contract.test_interactions.v1.DownloadRequest;
import ru.creditbank.contract.test_interactions.v1.FileChunk;
import ru.creditbank.contract.test_interactions.v1.TestInteractionsServiceGrpc;
import ru.creditbank.contract.test_interactions.v1.TestRequest;
import ru.creditbank.contract.test_interactions.v1.TestResponse;
import ru.creditbank.contract.test_interactions.v1.TransferProgress;
import ru.creditbank.contract.test_interactions.v1.UploadResult;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/grpc-test")
public class TestIntegrationControllerGrpc {

  private final TestInteractionsServiceGrpc.TestInteractionsServiceStub
      testInteractionsAsyncStub;

  private final TestInteractionsServiceGrpc.TestInteractionsServiceFutureStub
      testInteractionsFutureStub;

  @PostMapping("/async")
  public CompletableFuture<ResponseEntity<TestResponseDTO>> asyncCall(
      @RequestBody TestRequestDTO request
  ) {
    CompletableFuture<ResponseEntity<TestResponseDTO>> future =
        new CompletableFuture<>();

    TestRequest grpcRequest = TestRequest.newBuilder()
        .setMessage(request.message())
        .build();

    testInteractionsAsyncStub.unaryTest(
        grpcRequest,
        new StreamObserver<>() {

          @Override
          public void onNext(TestResponse value) {
            future.complete(ResponseEntity.ok(mapTestResponse(value)));
          }

          @Override
          public void onError(Throwable t) {
            future.completeExceptionally(t);
          }

          @Override
          public void onCompleted() {
            // Completion is handled after onNext()
          }
        }
    );

    return future;
  }

  @PostMapping("/future")
  public CompletableFuture<ResponseEntity<TestResponseDTO>> futureCall(
      @RequestBody TestRequestDTO request
  ) {
    CompletableFuture<ResponseEntity<TestResponseDTO>> future =
        new CompletableFuture<>();

    TestRequest grpcRequest = TestRequest.newBuilder()
        .setMessage(request.message())
        .build();

    ListenableFuture<TestResponse> listenableFuture =
        testInteractionsFutureStub.unaryTest(grpcRequest);

    Futures.addCallback(
        listenableFuture,
        new FutureCallback<>() {

          @Override
          public void onSuccess(TestResponse result) {
            future.complete(ResponseEntity.ok(mapTestResponse(result)));
          }

          @Override
          public void onFailure(Throwable t) {
            future.completeExceptionally(t);
          }
        },
        MoreExecutors.directExecutor()
    );

    return future;
  }

  @PostMapping(
      value = "/client-streaming",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public CompletableFuture<ResponseEntity<UploadResultDTO>> uploadFile(
      @RequestPart("file") MultipartFile file
  ) {
    CompletableFuture<ResponseEntity<UploadResultDTO>> result =
        new CompletableFuture<>();

    StreamObserver<UploadResult> clientObserver =
        new StreamObserver<>() {

          @Override
          public void onNext(UploadResult value) {
            result.complete(ResponseEntity.ok(mapUploadResult(value)));
          }

          @Override
          public void onError(Throwable t) {
            result.completeExceptionally(t);
          }

          @Override
          public void onCompleted() {
            // Completion is handled after onNext()
          }
        };

    StreamObserver<FileChunk> serverObserver =
        testInteractionsAsyncStub.uploadFile(clientObserver);

    int chunkSize = 3 * 1024 * 1024;
    byte[] buffer = new byte[chunkSize];

    try (InputStream inputStream = file.getInputStream()) {

      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {

        FileChunk chunk = FileChunk.newBuilder()
            .setFileName(file.getOriginalFilename())
            .setData(ByteString.copyFrom(buffer, 0, bytesRead))
            .build();

        serverObserver.onNext(chunk);
      }

      serverObserver.onCompleted();

    } catch (Exception e) {
      serverObserver.onError(e);
    }

    return result;
  }

  @PostMapping("/server-streaming")
  public CompletableFuture<ResponseEntity<List<FileChunkDTO>>> downloadFile(
      @RequestBody DownloadRequestDTO request
  ) {
    CompletableFuture<ResponseEntity<List<FileChunkDTO>>> result =
        new CompletableFuture<>();

    List<FileChunkDTO> chunks = new ArrayList<>();

    DownloadRequest grpcRequest = DownloadRequest.newBuilder()
        .setFileName(request.fileName())
        .build();

    testInteractionsAsyncStub.downloadFile(
        grpcRequest,
        new StreamObserver<>() {

          @Override
          public void onNext(FileChunk value) {
            chunks.add(mapFileChunk(value));
          }

          @Override
          public void onError(Throwable t) {
            result.completeExceptionally(t);
          }

          @Override
          public void onCompleted() {
            result.complete(ResponseEntity.ok(chunks));
          }
        }
    );

    return result;
  }

  @PostMapping("/bidirectional-streaming")
  public CompletableFuture<ResponseEntity<List<TransferProgressDTO>>> transfer(
      @RequestBody List<FileChunkDTO> chunks
  ) {
    CompletableFuture<ResponseEntity<List<TransferProgressDTO>>> result =
        new CompletableFuture<>();

    List<TransferProgressDTO> progressList =
        new ArrayList<>();

    StreamObserver<TransferProgress> clientObserver =
        new StreamObserver<>() {

          @Override
          public void onNext(TransferProgress progress) {
            progressList.add(mapTransferProgress(progress));
          }

          @Override
          public void onError(Throwable t) {
            result.completeExceptionally(t);
          }

          @Override
          public void onCompleted() {
            result.complete(ResponseEntity.ok(progressList));
          }
        };

    StreamObserver<FileChunk> serverObserver =
        testInteractionsAsyncStub.transfer(clientObserver);

    for (FileChunkDTO chunkDTO : chunks) {
      FileChunk.Builder builder = FileChunk.newBuilder()
          .setFileName(chunkDTO.fileName())
          .setData(ByteString.copyFrom(Base64.getDecoder().decode(chunkDTO.data())))
          .setChunkNumber(chunkDTO.chunkNumber());

      if (chunkDTO.totalBytes() != null) {
        builder.setTotalBytes(chunkDTO.totalBytes());
      }

      serverObserver.onNext(builder.build());
    }

    serverObserver.onCompleted();

    return result;
  }

  private TestResponseDTO mapTestResponse(
      TestResponse response
  ) {
    return new TestResponseDTO(
        response.getMessage()
    );
  }

  private UploadResultDTO mapUploadResult(
      UploadResult response
  ) {
    return new UploadResultDTO(
        response.getFileName(),
        response.getTotalBytes(),
        response.getChunksCount()
    );
  }

  private FileChunkDTO mapFileChunk(
      FileChunk chunk
  ) {
    return new FileChunkDTO(
        chunk.getFileName(),
        chunk.getData().toStringUtf8(),
        chunk.getChunkNumber(),
        chunk.hasTotalBytes()
            ? chunk.getTotalBytes()
            : null
    );
  }

  private TransferProgressDTO mapTransferProgress(
      TransferProgress progress
  ) {
    return new TransferProgressDTO(
        progress.getBytesProcessed(),
        progress.getTotalBytes(),
        progress.getPercent()
    );
  }
}