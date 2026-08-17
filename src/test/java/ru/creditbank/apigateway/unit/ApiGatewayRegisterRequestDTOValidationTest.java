package ru.creditbank.apigateway.unit;


import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.creditbank.apigateway.auth.dto.register.FullNameDTO;
import ru.creditbank.apigateway.auth.dto.register.RegisterRequestDTO;

class ApiGatewayRegisterRequestDTOValidationTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation
        .buildDefaultValidatorFactory()
        .getValidator();
  }

  @Test
  void shouldPassValidationForValidRequest() {
    RegisterRequestDTO request = new RegisterRequestDTO(
        "example@mail.ru",
        validFullName(),
        "Credit123"
    );

    Set<ConstraintViolation<RegisterRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void shouldFailWhenEmailIsBlank() {
    RegisterRequestDTO request = new RegisterRequestDTO(
        "",
        validFullName(),
        "Credit123"
    );

    Set<ConstraintViolation<RegisterRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("email");
  }

  @Test
  void shouldFailWhenEmailHasInvalidFormat() {
    RegisterRequestDTO request = new RegisterRequestDTO(
        "example@mail",
        validFullName(),
        "Credit123"
    );

    Set<ConstraintViolation<RegisterRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("email");
  }

  @Test
  void shouldFailWhenFullNameIsNull() {
    RegisterRequestDTO request = new RegisterRequestDTO(
        "example@mail.ru",
        null,
        "Credit123"
    );

    Set<ConstraintViolation<RegisterRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("fullName");
  }

  @Test
  void shouldFailWhenFirstNameIsInvalid() {
    RegisterRequestDTO request = new RegisterRequestDTO(
        "example@mail.ru",
        new FullNameDTO(
            "Иван123",
            "Иванов",
            "Иванович"
        ),
        "Credit123"
    );

    Set<ConstraintViolation<RegisterRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("fullName.firstName");
  }

  @Test
  void shouldPassValidationWhenMiddleNameIsNull() {
    RegisterRequestDTO request = new RegisterRequestDTO(
        "example@mail.ru",
        new FullNameDTO(
            "Иван",
            "Иванов",
            null
        ),
        "Credit123"
    );

    Set<ConstraintViolation<RegisterRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void shouldFailWhenPasswordIsBlank() {
    RegisterRequestDTO request = new RegisterRequestDTO(
        "example@mail.ru",
        validFullName(),
        ""
    );

    Set<ConstraintViolation<RegisterRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("password");
  }

  @Test
  void shouldFailWhenPasswordDoesNotMeetRequirements() {
    RegisterRequestDTO request = new RegisterRequestDTO(
        "example@mail.ru",
        validFullName(),
        "credit123"
    );

    Set<ConstraintViolation<RegisterRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(violation ->
            violation.getPropertyPath().toString())
        .contains("password");
  }

  private FullNameDTO validFullName() {
    return new FullNameDTO(
        "Иван",
        "Иванов",
        "Иванович"
    );
  }
}
