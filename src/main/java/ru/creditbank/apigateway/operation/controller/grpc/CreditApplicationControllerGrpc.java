package ru.creditbank.apigateway.operation.controller.grpc;

import com.google.protobuf.Timestamp;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.apigateway.operation.dto.application.CreditApplicationRequestDTO;
import ru.creditbank.apigateway.operation.dto.application.CreditApplicationResponseDTO;
import ru.creditbank.apigateway.operation.dto.application.Status;
import ru.creditbank.apigateway.operation.dto.details.CreditApplicationDetailsDTO;
import ru.creditbank.apigateway.operation.dto.details.LoanDetailsDTO;
import ru.creditbank.apigateway.operation.dto.details.UserInfoDTO;
import ru.creditbank.apigateway.operation.dto.status.StatusUpdateRequestDTO;
import ru.creditbank.apigateway.operation.dto.status.UpdateStatusDTO;
import ru.creditbank.contract.credit.v1.CreditApplicationDetailsRequest;
import ru.creditbank.contract.credit.v1.CreditApplicationDetailsResponse;
import ru.creditbank.contract.credit.v1.CreditApplicationRequest;
import ru.creditbank.contract.credit.v1.CreditApplicationResponse;
import ru.creditbank.contract.credit.v1.CreditServiceGrpc;
import ru.creditbank.contract.credit.v1.CreditStatus;
import ru.creditbank.contract.credit.v1.LoanDetails;
import ru.creditbank.contract.credit.v1.StatusUpdateRequest;
import ru.creditbank.contract.credit.v1.UpdateStatus;
import ru.creditbank.contract.credit.v1.UserInfo;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/grpc/credit-service/api/v1/credit")
public class CreditApplicationControllerGrpc {

  private final CreditServiceGrpc.CreditServiceBlockingStub creditServiceBlockingStub;

  @PostMapping
  public ResponseEntity<CreditApplicationResponseDTO> createApplication(
      @Valid @RequestBody CreditApplicationRequestDTO request
  ) {
    log.info("Received credit application request");

    CreditApplicationRequest grpcRequest =
        CreditApplicationRequest.newBuilder()
            .setFullName(request.fullName())
            .setRequestedAmount(request.requestedAmount().toPlainString())
            .setTermMonths(request.termMonths())
            .build();

    CreditApplicationResponse grpcResponse =
        creditServiceBlockingStub.createApplication(grpcRequest);

    return ResponseEntity.ok(new CreditApplicationResponseDTO(
        UUID.fromString(grpcResponse.getId()),
        mapCreditStatusToDto(grpcResponse.getStatus()),
        mapTimestampToDto(grpcResponse.getCreatedAt())
    ));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CreditApplicationDetailsDTO> getApplicationDetails(
      @PathVariable UUID id
  ) {
    log.info("Received request for application details with application id = {}", id);

    CreditApplicationDetailsRequest grpcRequest =
        CreditApplicationDetailsRequest.newBuilder()
            .setId(id.toString())
            .build();

    CreditApplicationDetailsResponse grpcResponse =
        creditServiceBlockingStub.getApplicationDetails(grpcRequest);

    return ResponseEntity.ok(new CreditApplicationDetailsDTO(
        UUID.fromString(grpcResponse.getId()),
        mapUserInfoToDto(grpcResponse),
        mapLoanDetailsToDto(grpcResponse),
        mapCreditStatusToDto(grpcResponse.getStatus()),
        mapTimestampToDto(grpcResponse.getCreatedAt())
    ));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> changeApplicationStatus(
      @PathVariable UUID id,
      @RequestBody @Valid StatusUpdateRequestDTO request
  ) {
    log.info("Received request for application id = {}, new status = {}", id, request.status());

    StatusUpdateRequest.Builder grpcRequest =
        StatusUpdateRequest.newBuilder()
            .setId(id.toString())
            .setStatus(mapCreditStatusToGrpc(request.status()));

    if (request.managerComment() != null) {
      grpcRequest.setManagerComment(request.managerComment());
    }

    if (request.interestRate() != null) {
      grpcRequest.setInterestRate(request.interestRate().toPlainString());
    }

    //noinspection ResultOfMethodCallIgnored
    creditServiceBlockingStub.changeApplicationStatus(grpcRequest.build());

    return ResponseEntity.noContent().build();
  }

  private UserInfoDTO mapUserInfoToDto(CreditApplicationDetailsResponse grpcResponse) {
    if (!grpcResponse.hasUserInfo()) {
      return null;
    }

    UserInfo userInfo = grpcResponse.getUserInfo();

    return new UserInfoDTO(
        userInfo.getUserId(),
        userInfo.getFullName(),
        userInfo.getEmail()
    );
  }

  private LoanDetailsDTO mapLoanDetailsToDto(CreditApplicationDetailsResponse grpcResponse) {
    if (!grpcResponse.hasLoanDetails()) {
      return null;
    }

    LoanDetails loanDetails = grpcResponse.getLoanDetails();

    return new LoanDetailsDTO(
        new BigDecimal(loanDetails.getRequestedAmount()),
        loanDetails.getTermMonths(),
        loanDetails.hasInterestRate() ? new BigDecimal(loanDetails.getInterestRate()) : null
    );
  }

  private Status mapCreditStatusToDto(CreditStatus grpcStatus) {
    return switch (grpcStatus) {
      case CREDIT_STATUS_PENDING -> Status.PENDING;
      case CREDIT_STATUS_APPROVING -> Status.APPROVING;
      case CREDIT_STATUS_APPROVED -> Status.APPROVED;
      case CREDIT_STATUS_REJECTED -> Status.REJECTED;

      case CREDIT_STATUS_UNSPECIFIED, UNRECOGNIZED -> throw new IllegalStateException(
          "Unexpected credit status: " + grpcStatus
      );
    };
  }

  private Instant mapTimestampToDto(Timestamp grpcTimestamp) {
    return Instant.ofEpochSecond(
        grpcTimestamp.getSeconds(),
        grpcTimestamp.getNanos()
    );
  }

  private UpdateStatus mapCreditStatusToGrpc(UpdateStatusDTO statusDTO) {
    return switch (statusDTO) {
      case APPROVING -> UpdateStatus.UPDATE_STATUS_APPROVING;
      case REJECTED -> UpdateStatus.UPDATE_STATUS_REJECTED;
    };
  }
}
