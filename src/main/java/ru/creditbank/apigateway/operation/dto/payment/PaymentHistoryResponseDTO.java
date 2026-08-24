package ru.creditbank.apigateway.operation.dto.payment;

import java.util.List;

public record PaymentHistoryResponseDTO(
    List<PaymentHistoryItemDTO> payments
) {

}
