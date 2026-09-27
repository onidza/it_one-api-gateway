package ru.creditbank.apigateway.auth.controller;

import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller for verifying Spring Security authentication and authorization.
 *
 * <p>Provides endpoints for checking claims and permissions via
 * {@link AuthenticationPrincipal} and roles {@code ADMIN} and {@code CREDIT_MANAGER}
 *
 * <p>Available only with profile {@code dev} enabled.
 */
@Profile("dev")
@RestController
@RequestMapping("/api/v1/test")
public class AdmissionTestController {

  @GetMapping("/me")
  public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
    return jwt.getClaims();
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String admin() {
    return "admin access";
  }

  @GetMapping("/credit-manager")
  @PreAuthorize("hasRole('CREDIT_MANAGER')")
  public String creditManager() {
    return "credit manager access";
  }
}