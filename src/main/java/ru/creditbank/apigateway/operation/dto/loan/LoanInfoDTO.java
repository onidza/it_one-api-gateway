package ru.creditbank.apigateway.operation.dto.loan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LoanInfoDTO(
    UUID loanId,
    BigDecimal totalAmount,
    BigDecimal remainingAmount,
    LocalDate nextPaymentDate,
    LoanStatus status
) {

}
