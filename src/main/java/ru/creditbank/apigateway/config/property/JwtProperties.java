package ru.creditbank.apigateway.config.property;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
    String issuer,
    Duration accessTokenTtl,
    String secret,
    Duration refreshTokenTtl
) {

}
