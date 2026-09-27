package ru.creditbank.apigateway.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpHeaders;

/**
 * Wrapper for inject bearer access token header for each http request,
 * cus {@link HttpServletRequest} is immutable.
 *
 * @see JwtHeaderInjectionFilter
 */
public class AuthorizationHeaderRequestWrapper extends HttpServletRequestWrapper {

  private final String authorizationHeader;

  public AuthorizationHeaderRequestWrapper(
      HttpServletRequest request,
      String accessToken
  ) {
    super(request);
    this.authorizationHeader = "Bearer " + accessToken;
  }

  @Override
  public String getHeader(String name) {
    if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)) {
      return authorizationHeader;
    }

    return super.getHeader(name);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)) {
      return Collections.enumeration(
          List.of(authorizationHeader)
      );
    }

    return super.getHeaders(name);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    Set<String> headerNames = new LinkedHashSet<>();

    Enumeration<String> existingHeaders =
        super.getHeaderNames();

    if (existingHeaders != null) {
      existingHeaders.asIterator()
          .forEachRemaining(headerNames::add);
    }

    headerNames.add(HttpHeaders.AUTHORIZATION);

    return Collections.enumeration(headerNames);
  }
}
