package ru.creditbank.apigateway.operation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

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
                example = "50000.00"
        )
        @NotNull
        @Positive
        @Digits(integer = 15, fraction = 2)
        BigDecimal requestedAmount,

        @Schema(
                description = "Срок кредита в месяцах",
                example = "12"
        )
        @NotNull
        @Min(1)
        Integer termMonths
) {}
