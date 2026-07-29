package ru.creditbank.apigateway.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.creditbank.apigateway.auth.entity.UserEntity;
import ru.creditbank.apigateway.auth.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGenerationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

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

        Optional<String> token = userRepository
                .findFirstByAccessTokenIsNotNullOrderByAccessTokenIssuedAtDesc()
                .map(UserEntity::getAccessToken);

        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletRequest wrappedRequest =
                new AuthorizationHeaderRequestWrapper(
                        request,
                        token.get()
                );

        log.debug(
                "Injected mock Authorization header: method={}, uri={}",
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
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }
}