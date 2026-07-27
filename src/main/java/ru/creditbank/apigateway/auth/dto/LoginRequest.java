package ru.creditbank.apigateway.auth.dto;

import jakarta.validation.constraints.*;

public record LoginRequest(

        @NotBlank
        @Email
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String email,

        @NotBlank
        @Size(min = 8, max = 64)
        String password
) {}
