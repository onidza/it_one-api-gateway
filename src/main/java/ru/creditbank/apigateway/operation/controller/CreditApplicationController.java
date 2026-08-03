package ru.creditbank.apigateway.operation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import ru.creditbank.apigateway.operation.dto.CreditApplicationRequestDTO;
import ru.creditbank.apigateway.operation.dto.CreditApplicationResponseDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CreditApplicationController {

  private final RestClient creditServiceRestClient;

  @PostMapping("/credit")
  public CreditApplicationResponseDTO createApplication(
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
        .body(CreditApplicationResponseDTO.class);
  }
}
