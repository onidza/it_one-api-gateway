package ru.creditbank.apigateway.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import ru.creditbank.apigateway.auth.dto.error.ErrorResponseDTO;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
            BadCredentialsException exception
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExists(
            UserAlreadyExistsException exception
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<byte[]> handleRestClientResponseException(
            RestClientResponseException exception
    ) {
        log.warn(
                "Downstream service returned status = {}",
                exception.getStatusCode()
        );

        HttpHeaders headers = new HttpHeaders();

        if (exception.getResponseHeaders() != null
                && exception.getResponseHeaders().getContentType() != null) {
            headers.setContentType(
                    exception.getResponseHeaders().getContentType()
            );
        }

        return new ResponseEntity<>(
                exception.getResponseBodyAsByteArray(),
                headers,
                exception.getStatusCode()
        );
    }
}
