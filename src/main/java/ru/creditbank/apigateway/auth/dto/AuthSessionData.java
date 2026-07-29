package ru.creditbank.apigateway.auth.dto;

import java.io.Serializable;
import java.time.Instant;

public record AuthSessionData(
        Long userId,
        String accessToken,
        String refreshToken,
        Instant refreshTokenExpiresAt
) implements Serializable {

    public AuthSessionData withAccessToken(
            String newAccessToken
    ) {
        return new AuthSessionData(
                userId,
                newAccessToken,
                refreshToken,
                refreshTokenExpiresAt
        );
    }
}
