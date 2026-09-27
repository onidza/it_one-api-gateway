package ru.creditbank.apigateway.config;

import java.time.Duration;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Rest client config for {@code credit service} and for {@code loan service}.
 */

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
  public RestClient creditServiceRestClient(RestClient.Builder builder) {
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

    return builder
        .baseUrl(creditServiceUrl)
        .requestFactory(requestFactory)
        .build();
  }

  @Bean
  public RestClient loanServiceRestClient(RestClient.Builder builder) {
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

    return builder
        .baseUrl(loanServiceUrl)
        .requestFactory(requestFactory)
        .build();
  }

  /**
   * Injecting {@code X-Trace-Id} header in rest clients.
   * @return RestClient.Builder for general rest configs
   */
  @Bean
  public RestClient.Builder restClientBuilder() {
    return RestClient.builder()
        .requestInterceptor((request, body, execution) -> {

          String traceId = MDC.get("traceId");

          if (traceId != null) {
            request.getHeaders()
                .set("X-Trace-Id", traceId);
          }

          return execution.execute(request, body);
        });
  }
}