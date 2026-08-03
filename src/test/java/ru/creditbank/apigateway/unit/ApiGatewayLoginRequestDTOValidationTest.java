package ru.creditbank.apigateway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.creditbank.apigateway.auth.dto.login.LoginRequestDTO;

class ApiGatewayLoginRequestDTOValidationTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation
        .buildDefaultValidatorFactory()
        .getValidator();
  }

  @Test
  void shouldPassValidationForValidRequest() {
    LoginRequestDTO request = new LoginRequestDTO(
        "example@mail.ru",
        "Credit123"
    );

    Set<ConstraintViolation<LoginRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void shouldFailWhenEmailIsBlank() {
    LoginRequestDTO request = new LoginRequestDTO(
        " ",
        "Credit123"
    );

    Set<ConstraintViolation<LoginRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("email");
  }

  @Test
  void shouldFailWhenEmailHasInvalidFormat() {
    LoginRequestDTO request = new LoginRequestDTO(
        "example@mail",
        "Credit123"
    );

    Set<ConstraintViolation<LoginRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("email");
  }

  @Test
  void shouldFailWhenPasswordIsBlank() {
    LoginRequestDTO request = new LoginRequestDTO(
        "example@mail.ru",
        " "
    );

    Set<ConstraintViolation<LoginRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("password");
  }

  @Test
  void shouldFailWhenPasswordIsTooShort() {
    LoginRequestDTO request = new LoginRequestDTO(
        "example@mail.ru",
        "Credit1"
    );

    Set<ConstraintViolation<LoginRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("password");
  }

  @Test
  void shouldFailWhenPasswordIsTooLong() {
    LoginRequestDTO request = new LoginRequestDTO(
        "example@mail.ru",
        "A1" + "a".repeat(63)
    );

    Set<ConstraintViolation<LoginRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("password");
  }
}
