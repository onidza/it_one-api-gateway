package ru.creditbank.apigateway.operation.dto.application;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreditApplicationRequestDTO(

    @Schema(
        description = "Полное имя заявителя",
        example = "Ступницкий Иван Владимирович"
    )
    @NotBlank
    @Pattern(regexp = "^[А-Яа-яЁёA-Za-z\\s-]+$")
    @Size(min = 5, max = 100)
    String fullName,

    @Schema(
        description = "Запрашиваемая сумма кредита",
        example = "50000"
    )
    @NotNull
    @Positive
    BigDecimal requestedAmount,

    @Schema(
        description = "Срок кредита в месяцах",
        example = "12"
    )
    @NotNull
    @Min(1)
    Integer termMonths
) {

}
