package ru.creditbank.apigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String issuer,
        Duration accessTokenTtl,
        String secret,
        Duration refreshTokenTtl
) {}
