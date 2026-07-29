package ru.creditbank.apigateway.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpHeaders;

import java.util.*;

public class AuthorizationHeaderRequestWrapper
        extends HttpServletRequestWrapper {

    private final String authorizationHeader;

    public AuthorizationHeaderRequestWrapper(
            HttpServletRequest request,
            String token
    ) {
        super(request);
        this.authorizationHeader = "Bearer " + token;
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
