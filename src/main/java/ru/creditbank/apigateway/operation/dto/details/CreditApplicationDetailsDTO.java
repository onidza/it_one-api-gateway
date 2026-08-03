package ru.creditbank.apigateway.operation.dto.details;

import java.time.Instant;
import java.util.UUID;
import ru.creditbank.apigateway.operation.dto.application.Status;

public record CreditApplicationDetailsDTO(
  UUID id,
  UserInfoDTO userInfo,
  LoanDetailsDTO loanDetails,
  Status status,
  Instant createdAt
) {

}
