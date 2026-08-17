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
    @NotBlank(message = "Full name is required")
    @Pattern(
        regexp = "^[А-Яа-яЁёA-Za-z\\s-]+$",
        message = "Full name contains invalid characters"
    )
    @Size(
        min = 5,
        max = 100,
        message = "Full name must be between 5 and 100 characters"
    )
    String fullName,

    @Schema(
        description = "Запрашиваемая сумма кредита",
        example = "50000"
    )
    @NotNull(message = "Requested amount is required")
    @Positive(message = "Requested amount must be greater than 0")
    BigDecimal requestedAmount,

    @Schema(
        description = "Срок кредита в месяцах",
        example = "12"
    )
    @NotNull(message = "Loan term is required")
    @Min(
        value = 1,
        message = "Loan term must be at least 1 month"
    )
    Integer termMonths
) {

}
