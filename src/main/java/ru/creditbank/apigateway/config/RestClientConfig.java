package ru.creditbank.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.creditbank.apigateway.config.property.CreditServiceProperties;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient creditServiceRestClient(
            CreditServiceProperties properties
    ) {
        return RestClient.builder()
                .baseUrl(properties.url())
                .build();
    }
}
