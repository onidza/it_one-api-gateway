package ru.creditbank.apigateway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record RegisterRequestDTO(

        @Schema(
                description = "Email пользователя",
                example = "ivan-st233@mail.ru"
        )
        @JsonProperty("email")
        @NotBlank
        @Email
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String email,

        @JsonProperty("fullname")
        @NotNull
        @Valid
        FullNameDTO fullName,

        @Schema(
                description = "Пароль пользователя",
                example = "Credit123"
        )
        @NotBlank
        @Size(min = 8, max = 64)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")
        String password
) {}
