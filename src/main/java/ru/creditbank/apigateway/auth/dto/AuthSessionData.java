package ru.creditbank.apigateway.auth.dto;

import java.time.Instant;

public record AuthSessionData(
        Long userId,
        String accessToken,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {
    public AuthSessionData withAccessToken(String newAccessToken) {
        return new AuthSessionData(
                userId,
                newAccessToken,
                refreshToken,
                refreshTokenExpiresAt
        );
    }
}
