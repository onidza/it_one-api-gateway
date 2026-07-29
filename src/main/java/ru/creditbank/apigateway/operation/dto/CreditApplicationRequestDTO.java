package ru.creditbank.apigateway.operation.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreditApplicationRequestDTO(

        @NotBlank
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z\\s-]+$")
        @Size(min = 5, max = 100)
        String fullName,

        @NotNull
        @Positive
        @Digits(integer = 15, fraction = 2)
        BigDecimal requestedAmount,

        @NotNull
        @Min(1)
        Integer termMonths
) {}
