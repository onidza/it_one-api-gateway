package ru.creditbank.apigateway.operation.dto.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentHistoryItemDTO(
    UUID paymentId,
    BigDecimal amount,
    Instant paymentDate,
    PaymentType paymentType,
    BigDecimal newBalance
) {

}
