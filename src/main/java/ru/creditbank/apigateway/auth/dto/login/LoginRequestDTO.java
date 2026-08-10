package ru.creditbank.apigateway.auth.dto.login;

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
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "Invalid email format"
    )
    String email,

    @Schema(
        description = "Пароль пользователя",
        example = "Credit123"
    )
    @NotBlank(message = "Password is required")
    @Size(
        min = 8,
        max = 64,
        message = "Password must be between 8 and 64 characters"
    )
    String password
) {

}