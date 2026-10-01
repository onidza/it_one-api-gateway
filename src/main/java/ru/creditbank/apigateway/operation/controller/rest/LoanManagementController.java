package ru.creditbank.apigateway.operation.controller.rest;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import ru.creditbank.apigateway.operation.dto.loan.UserLoansResponseDTO;
import ru.creditbank.apigateway.operation.dto.payment.CreatePaymentRequestDTO;
import ru.creditbank.apigateway.operation.dto.payment.PaymentHistoryResponseDTO;
import ru.creditbank.apigateway.operation.dto.payment.PaymentResponseDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-management-service/api/v1/payment")
public class LoanManagementController {

  private final RestClient loanServiceRestClient;

  @GetMapping("/loans")
  public ResponseEntity<UserLoansResponseDTO> getUserLoans(
      @AuthenticationPrincipal Jwt accessToken
  ) {
    log.info("Received request for all user loans");

    return loanServiceRestClient.get()
        .uri("/loan-management-service/api/v1/payment/loans")
        .headers(headers ->
            headers.setBearerAuth(accessToken.getTokenValue()))
        .retrieve()
        .toEntity(UserLoansResponseDTO.class);
  }

  @PostMapping
  public ResponseEntity<PaymentResponseDTO> createPayment(
      @Valid @RequestBody CreatePaymentRequestDTO request,
      @AuthenticationPrincipal Jwt accessToken
  ) {
    log.info(
        "Received create payment request, loanId = {}, amount = {}, paymentType = {}",
        request.loanId(),
        request.amount(),
        request.paymentType()
    );

    return loanServiceRestClient.post()
        .uri("/loan-management-service/api/v1/payment")
        .headers(headers ->
            headers.setBearerAuth(accessToken.getTokenValue()))
        .body(request)
        .retrieve()
        .toEntity(PaymentResponseDTO.class);
  }

  @GetMapping("/history")
  public ResponseEntity<PaymentHistoryResponseDTO> getPaymentHistory(
      @RequestParam("loanId") UUID loanId,
      @AuthenticationPrincipal Jwt accessToken
  ) {
    log.info(
        "Received payment history request, loanId = {}",
        loanId
    );

    return loanServiceRestClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/loan-management-service/api/v1/payment/history")
            .queryParam("loanId", loanId)
            .build()
        )
        .headers(headers ->
            headers.setBearerAuth(accessToken.getTokenValue()))
        .retrieve()
        .toEntity(PaymentHistoryResponseDTO.class);
  }
}
