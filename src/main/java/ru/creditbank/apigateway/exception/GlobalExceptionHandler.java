package ru.creditbank.apigateway.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import ru.creditbank.apigateway.auth.dto.error.ErrorResponseDTO;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
      HttpServletRequest request
  ) {
    HttpStatus status = HttpStatus.FORBIDDEN;

    ErrorResponseDTO response = new ErrorResponseDTO(
        LocalDateTime.now(),
        status.value(),
        status.getReasonPhrase(),
        "Access denied",
        request.getRequestURI()
    );

    return ResponseEntity
        .status(status)
        .body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDTO> handleValidationException(
      MethodArgumentNotValidException ex,
      HttpServletRequest request
  ) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    String message = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .findFirst()
        .map(FieldError::getDefaultMessage)
        .orElse("Invalid request data");

    ErrorResponseDTO response = new ErrorResponseDTO(
        LocalDateTime.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI()
    );

    return ResponseEntity
        .status(status)
        .body(response);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
      BadCredentialsException ex,
      HttpServletRequest request
  ) {
    log.warn("Business exception: {} - {}",
        ex.getClass().getSimpleName(),
        ex.getMessage());

    ErrorResponseDTO response = new ErrorResponseDTO(
        LocalDateTime.now(),
        HttpStatus.UNAUTHORIZED.value(),
        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
        "Invalid email or password",
        request.getRequestURI()
    );

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(response);
  }

  @ExceptionHandler(RestClientResponseException.class)
  public ResponseEntity<ErrorResponseDTO> handleRestClientResponseException(
      RestClientResponseException ex,
      HttpServletRequest request
  ) {
    HttpStatusCode status = ex.getStatusCode();

    ErrorResponseDTO downstreamError;

    try {
      downstreamError =
          ex.getResponseBodyAs(ErrorResponseDTO.class);
    } catch (Exception e) {
      downstreamError = null;
    }

    String error;
    String message;

    if (downstreamError != null) {
      error = downstreamError.error();
      message = downstreamError.message();
    } else {
      HttpStatus httpStatus =
          HttpStatus.resolve(status.value());

      error = httpStatus != null
          ? httpStatus.getReasonPhrase()
          : "Downstream Error";

      message = "Downstream service request failed";
    }

    log.warn(
        "Downstream request failed: status = {}, error = {}, message = {}",
        status.value(),
        error,
        message
    );

    ErrorResponseDTO response = new ErrorResponseDTO(
        LocalDateTime.now(),
        status.value(),
        error,
        message,
        request.getRequestURI()
    );

    return ResponseEntity
        .status(status)
        .body(response);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponseDTO> handleBusinessException(
      BusinessException ex,
      HttpServletRequest request
  ) {
    log.warn(
        "Business exception: {} - {}",
        ex.getClass().getSimpleName(),
        ex.getMessage()
    );

    ErrorResponseDTO response = new ErrorResponseDTO(
        LocalDateTime.now(),
        ex.getStatus().value(),
        ex.getStatus().getReasonPhrase(),
        ex.getMessage(),
        request.getRequestURI()
    );

    return ResponseEntity
        .status(ex.getStatus())
        .body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGenericException(
      Exception ex, HttpServletRequest request) {
    log.error("Unexpected error", ex);

    ErrorResponseDTO response = new ErrorResponseDTO(
        LocalDateTime.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error",
        "An unexpected error occurred",
        request.getRequestURI()
    );
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(response);
  }
}
