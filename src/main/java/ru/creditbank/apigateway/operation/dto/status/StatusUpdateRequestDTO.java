package ru.creditbank.apigateway.operation.dto.status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StatusUpdateRequestDTO(

    @Schema(description = "Новый статус заявки", example = "APPROVED")
    @NotNull
    UpdateStatusDTO status,

    @Schema(
        description = "Комментарий менеджера (необязательно)",
        example = "Заявка одобрена, сумма кредита появится на счёте в течении часа")
    @Size(max = 500)
    String managerComment
) {

}
