package ru.creditbank.apigateway.operation.dto.status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record StatusUpdateRequestDTO(

    @Schema(description = "Новый статус заявки", example = "APPROVED")
    @NotNull
    UpdateStatusDTO status,

    @Schema(
        description = "Комментарий менеджера (необязательно)",
        example = "Заявка одобрена, сумма кредита появится на счёте в течении часа")
    @Size(max = 500)
    String managerComment,

    @Schema(
        description = "Назначенная процентная ставка",
        example = "12.3"
    )
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "100.00")
    @Digits(integer = 3, fraction = 2)
    BigDecimal interestRate
) {

}
