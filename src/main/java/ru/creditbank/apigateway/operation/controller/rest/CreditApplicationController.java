package ru.creditbank.apigateway.operation.controller.rest;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import ru.creditbank.apigateway.operation.dto.application.CreditApplicationRequestDTO;
import ru.creditbank.apigateway.operation.dto.application.CreditApplicationResponseDTO;
import ru.creditbank.apigateway.operation.dto.details.CreditApplicationDetailsDTO;
import ru.creditbank.apigateway.operation.dto.status.StatusUpdateRequestDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/credit-service/api/v1/credit")
public class CreditApplicationController {

  private final RestClient creditServiceRestClient;

  @PostMapping
  public ResponseEntity<CreditApplicationResponseDTO> createApplication(
      @Valid @RequestBody CreditApplicationRequestDTO request,
      @AuthenticationPrincipal Jwt accessToken
  ) {
    log.info("Received credit application request");

    return creditServiceRestClient.post()
        .uri("/credit-service/api/v1/credit")
        .headers(headers ->
            headers.setBearerAuth(accessToken.getTokenValue()))
        .body(request)
        .retrieve()
        .toEntity(CreditApplicationResponseDTO.class);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CreditApplicationDetailsDTO> getApplicationDetails(
      @PathVariable UUID id,
      @AuthenticationPrincipal Jwt accessToken
  ) {
    log.info("Received request for application details with application id = {}", id);

    return creditServiceRestClient.get()
        .uri("/credit-service/api/v1/credit/{id}", id)
        .headers(headers ->
            headers.setBearerAuth(accessToken.getTokenValue()))
        .retrieve()
        .toEntity(CreditApplicationDetailsDTO.class);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> changeApplicationStatus(
      @PathVariable UUID id,
      @RequestBody @Valid StatusUpdateRequestDTO request,
      @AuthenticationPrincipal Jwt accessToken
  ) {
    log.info("Received request for application id = {}, new status = {}", id, request.status());

    return creditServiceRestClient.patch()
        .uri("/credit-service/api/v1/credit/{id}/status", id)
        .headers(headers ->
            headers.setBearerAuth(accessToken.getTokenValue()))
        .body(request)
        .retrieve()
        .toBodilessEntity();
  }
}
