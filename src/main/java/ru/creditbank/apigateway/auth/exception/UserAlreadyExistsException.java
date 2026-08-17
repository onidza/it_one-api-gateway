package ru.creditbank.apigateway.auth.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BusinessException {

  public UserAlreadyExistsException(String email) {
    super(
        "User with email '%s' already exists".formatted(email),
        HttpStatus.CONFLICT
    );
  }
}
