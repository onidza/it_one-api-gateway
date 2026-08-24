package ru.creditbank.apigateway.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.creditbank.apigateway.auth.dto.session.AuthSessionData;
import ru.creditbank.apigateway.auth.dto.session.SessionAttribute;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHeaderInjectionFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    String existingAuthorization =
        request.getHeader(HttpHeaders.AUTHORIZATION);

    if (existingAuthorization != null) {
      filterChain.doFilter(request, response);
      return;
    }

    HttpSession session = request.getSession(false);
    if (session == null) {
      filterChain.doFilter(request, response);
      return;
    }

    Object attribute = session.getAttribute(SessionAttribute.AUTH_SESSION);
    if (!(attribute instanceof AuthSessionData sessionData)) {
      log.warn("Invalid auth session");
      session.invalidate();

      filterChain.doFilter(request, response);
      return;
    }

    HttpServletRequest wrappedRequest =
        new AuthorizationHeaderRequestWrapper(
            request,
            sessionData.accessToken()
        );

    log.debug(
        "Injected mock Authorization header: method = {}, uri = {}",
        request.getMethod(),
        request.getRequestURI()
    );

    filterChain.doFilter(wrappedRequest, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();

    return path.equals("/api/v1/auth/register")
        || path.equals("/api/v1/auth/login")
        || path.equals("/api/v1/auth/refresh")
        || path.equals("/error")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs");
  }
}