package ru.creditbank.apigateway.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Value("${rest.credit-service.url}")
  private String creditServiceUrl;

  @Value("${rest.credit-service.connect-timeout}")
  private Duration creditServiceConnectionTimeout;

  @Value("${rest.credit-service.read-timeout}")
  private Duration creditServiceReadTimeout;

  @Value("${rest.loan-service.url}")
  private String loanServiceUrl;

  @Value("${rest.loan-service.connect-timeout}")
  private Duration loanServiceConnectionTimeout;

  @Value("${rest.loan-service.read-timeout}")
  private Duration loanServiceReadTimeout;

  @Bean
  public RestClient creditServiceRestClient() {
    ClientHttpRequestFactorySettings settings =
        ClientHttpRequestFactorySettings.defaults()
            .withTimeouts(
                creditServiceConnectionTimeout,
                creditServiceReadTimeout
            );

    ClientHttpRequestFactory requestFactory =
        ClientHttpRequestFactoryBuilder
            .detect()
            .build(settings);

    return RestClient.builder()
        .baseUrl(creditServiceUrl)
        .requestFactory(requestFactory)
        .build();
  }

  @Bean
  public RestClient loanServiceRestClient() {
    ClientHttpRequestFactorySettings settings =
        ClientHttpRequestFactorySettings.defaults()
            .withTimeouts(
                loanServiceConnectionTimeout,
                loanServiceReadTimeout
            );

    ClientHttpRequestFactory requestFactory =
        ClientHttpRequestFactoryBuilder
            .detect()
            .build(settings);

    return RestClient.builder()
        .baseUrl(loanServiceUrl)
        .requestFactory(requestFactory)
        .build();
  }
}
