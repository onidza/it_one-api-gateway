package ru.creditbank.apigateway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.creditbank.apigateway.auth.dto.token.RefreshTokenRequestDTO;

class ApiGatewayRefreshTokenDTOValidationTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation
        .buildDefaultValidatorFactory()
        .getValidator();
  }

  @Test
  void shouldPassValidationForValidRefreshToken() {
    RefreshTokenRequestDTO request =
        new RefreshTokenRequestDTO("valid-refresh-token");

    Set<ConstraintViolation<RefreshTokenRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void shouldFailWhenRefreshTokenIsBlank() {
    RefreshTokenRequestDTO request =
        new RefreshTokenRequestDTO(" ");

    Set<ConstraintViolation<RefreshTokenRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("refreshToken");
  }

  @Test
  void shouldFailWhenRefreshTokenIsTooLong() {
    RefreshTokenRequestDTO request =
        new RefreshTokenRequestDTO("a".repeat(4097));

    Set<ConstraintViolation<RefreshTokenRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("refreshToken");
  }
}
