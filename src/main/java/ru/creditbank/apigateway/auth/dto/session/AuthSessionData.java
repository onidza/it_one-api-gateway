package ru.creditbank.apigateway.auth.dto.session;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record AuthSessionData(
    UUID userId,
    String accessToken,
    String refreshToken,
    Instant refreshTokenExpiresAt
) implements Serializable {

  public AuthSessionData withAccessToken(String newAccessToken) {
    return new AuthSessionData(
        userId,
        newAccessToken,
        refreshToken,
        refreshTokenExpiresAt
    );
  }
}
