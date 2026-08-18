package ru.creditbank.apigateway.operation.dto.status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record StatusUpdateRequestDTO(

    @Schema(
        description = "Новый статус заявки",
        example = "APPROVING"
    )
    @NotNull(message = "Status is required")
    UpdateStatusDTO status,

    @Schema(
        description = "Комментарий менеджера (необязательно)",
        example = "Заявка одобрена, сумма кредита появится на счёте в течении часа"
    )
    @Size(
        max = 500,
        message = "Manager comment must not exceed 500 characters"
    )
    String managerComment,

    @Schema(
        description = "Назначенная процентная ставка",
        example = "12.3"
    )
    @DecimalMin(
        value = "0.01",
        message = "Interest rate must be at least 0.01"
    )
    @DecimalMax(
        value = "100.00",
        message = "Interest rate must not exceed 100.00"
    )
    @Digits(
        integer = 3,
        fraction = 2,
        message = "Interest rate must have up to 3 integer digits and 2 decimal places"
    )
    BigDecimal interestRate
) {

}
