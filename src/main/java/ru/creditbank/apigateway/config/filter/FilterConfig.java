package ru.creditbank.apigateway.config.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Register filter {@link TraceFilter} for each url pattern {@code /*} and order {@code №1}
 */
@Configuration
@RequiredArgsConstructor
public class FilterConfig {

  private final TraceFilter traceFilter;

  @Bean
  public FilterRegistrationBean<TraceFilter> traceFilterRegistration() {
    FilterRegistrationBean<TraceFilter> registration =
        new FilterRegistrationBean<>();

    registration.setFilter(traceFilter);
    registration.addUrlPatterns("/*");
    registration.setOrder(1);

    return registration;
  }
}