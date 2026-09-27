package ru.creditbank.apigateway.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter generate and set header {@code X-Trace-Id} for {@link HttpServletRequest}.
 * Also generate span id and put it with service name into {@code MDC} context.
 *
 * @see FilterConfig registration details
 */
@Component
@RequiredArgsConstructor
public class TraceFilter extends OncePerRequestFilter {

  @Value("${spring.application.name}")
  private String serviceName;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String traceId = request.getHeader("X-Trace-Id");
    if (traceId == null || traceId.isBlank()) {
      traceId = UUID.randomUUID().toString();
    }

    String spanId = UUID.randomUUID().toString();

    try {
      MDC.put("spanId", spanId);
      MDC.put("traceId", traceId);
      MDC.put("service-name", serviceName);

      response.setHeader("X-Trace-Id", traceId);

      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}