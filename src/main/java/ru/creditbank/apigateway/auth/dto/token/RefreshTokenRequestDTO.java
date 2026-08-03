package ru.creditbank.apigateway.auth.dto.token;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshTokenRequestDTO(

    @NotBlank(message = "Refresh token must not be blank")
    @Size(max = 4096, message = "Refresh token is too long")
    String refreshToken
) {

}
