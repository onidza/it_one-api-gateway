package ru.creditbank.apigateway.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rest.credit-service")
public record CreditServiceProperties(
    String url
) {

}
