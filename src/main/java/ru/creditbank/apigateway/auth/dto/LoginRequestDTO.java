package ru.creditbank.apigateway.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(

        @Schema(
                description = "Email пользователя",
                example = "ivan-st233@mail.ru"
        )
        @NotBlank
        @Email
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String email,

        @Schema(
                description = "Пароль пользователя",
                example = "Credit123"
        )
        @NotBlank
        @Size(min = 8, max = 64)
        String password
) {}
