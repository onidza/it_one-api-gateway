package ru.creditbank.apigateway.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import ru.creditbank.apigateway.config.property.CreditServiceProperties;

@Configuration
public class RestClientConfig {

  @Value("${rest.credit-service.connect-timeout}")
  private Duration connectionTimeout;

  @Value("${rest.credit-service.read-timeout}")
  private Duration readTimeout;

  @Bean
  public RestClient creditServiceRestClient(
      CreditServiceProperties properties
  ) {
    ClientHttpRequestFactorySettings settings =
        ClientHttpRequestFactorySettings.defaults()
            .withTimeouts(
                connectionTimeout,
                readTimeout
            );

    ClientHttpRequestFactory requestFactory =
        ClientHttpRequestFactoryBuilder
            .detect()
            .build(settings);

    return RestClient.builder()
        .baseUrl(properties.url())
        .requestFactory(requestFactory)
        .build();
  }
}
