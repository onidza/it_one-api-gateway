package ru.creditbank.apigateway.auth.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String issuer,
        Duration accessTokenTtl,
        String secret
) {}
