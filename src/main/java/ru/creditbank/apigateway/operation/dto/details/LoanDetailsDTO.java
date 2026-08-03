package ru.creditbank.apigateway.operation.dto.details;

import java.math.BigDecimal;

public record LoanDetailsDTO(
    BigDecimal requestedAmount,
    Integer termMonths,
    BigDecimal interestRate
) {

}
