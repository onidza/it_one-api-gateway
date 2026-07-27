package ru.creditbank.apigateway.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FullNameRequest(

        @NotNull
        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        String firstName,

        @NotNull
        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        String lastName,

        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        String middleName
) {}
