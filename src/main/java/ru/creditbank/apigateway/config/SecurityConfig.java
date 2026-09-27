package ru.creditbank.apigateway.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import ru.creditbank.apigateway.auth.dto.login.LoginRequestDTO;
import ru.creditbank.apigateway.auth.jwt.JwtHeaderInjectionFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationConverter jwtAuthenticationConverter;
  private final JwtHeaderInjectionFilter jwtHeaderInjectionFilter;

  /**
   * @return {@link BCryptPasswordEncoder} password encoder for {@link AuthenticationManager}
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Authentication provider for basic auth.
   * @see ru.creditbank.apigateway.auth.service.AuthService#login(LoginRequestDTO, HttpServletRequest)
   * @return provider manager for basic auth
   */
  @Bean
  public AuthenticationManager authenticationManager(
      UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder
  ) {
    DaoAuthenticationProvider provider =
        new DaoAuthenticationProvider(userDetailsService);

    provider.setPasswordEncoder(passwordEncoder);

    return new ProviderManager(provider);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/v1/auth/register",
                "/api/v1/auth/login",
                "/api/v1/auth/refresh"
            ).permitAll()

            .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/v3/api-docs.yaml",
                "/error")
            .permitAll()

            .requestMatchers(HttpMethod.POST, "/credit-service/api/v1/credit")
            .hasRole("USER")

            .requestMatchers(HttpMethod.GET, "/credit-service/api/v1/credit/{id}")
            .hasRole("CREDIT_MANAGER")

            .requestMatchers(HttpMethod.PATCH, "/credit-service/api/v1/credit/{id}/status")
            .hasRole("CREDIT_MANAGER")

            .requestMatchers(HttpMethod.GET,
                "/loan-management-service/api/v1/payment/loans",
                "/loan-management-service/api/v1/payment/history"
            )
            .hasRole("USER")

            .requestMatchers(HttpMethod.POST, "/loan-management-service/api/v1/payment")
            .hasRole("USER")

            .requestMatchers("/actuator/health").permitAll()

            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(
                SessionCreationPolicy.IF_REQUIRED
            )
            .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::none
            )
        )
        .oauth2ResourceServer(resourceServer ->
            resourceServer.jwt(jwt ->
                jwt.jwtAuthenticationConverter(
                    jwtAuthenticationConverter
                )
            )
        )
        .addFilterBefore(
            jwtHeaderInjectionFilter,
            BearerTokenAuthenticationFilter.class
        )
        .build();
  }
}
