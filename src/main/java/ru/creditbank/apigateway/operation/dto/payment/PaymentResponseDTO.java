package ru.creditbank.apigateway.operation.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentResponseDTO(
    UUID paymentId,
    BigDecimal newBalance,
    LocalDate nextPaymentDate
) {

}
