package ru.creditbank.apigateway.integrational;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.creditbank.apigateway.config.TestcontainersConfiguration;
import ru.creditbank.apigateway.operation.dto.CreditApplicationRequestDTO;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class ApiGatewayRoutingIntegrationTest {

  private static final String GATEWAY_PATH =
      "/api/v1/credit";

  private static final String CREDIT_SERVICE_PATH =
      "/credit-service/api/v1/credit";

  @RegisterExtension
  static WireMockExtension creditServiceMock =
      WireMockExtension.newInstance()
          .options(
              wireMockConfig()
                  .dynamicPort()
                  .http2PlainDisabled(true)
          )
          .build();

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "rest.credit-service.url",
        creditServiceMock::baseUrl
    );
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldRouteCreditApplicationToCreditService() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID applicationId = UUID.randomUUID();

    CreditApplicationRequestDTO request =
        new CreditApplicationRequestDTO(
            "Иван Иванов Иванович",
            new BigDecimal("50000"),
            12
        );

    String creditServiceResponse = """
        {
          "id": "%s",
          "status": "PENDING",
          "createdAt": "2026-08-03T12:00:00Z"
        }
        """.formatted(applicationId);

    creditServiceMock.stubFor(
        WireMock.post(urlEqualTo("/credit-service/api/v1/credit"))
            .willReturn(
                okJson(creditServiceResponse)
            )
    );

    mockMvc.perform(
            post(GATEWAY_PATH)
                .with(jwt()
                    .jwt(jwt -> jwt
                        .tokenValue("test-token")
                        .claim(
                            "user_id",
                            userId.toString()
                        )
                    )
                    .authorities(
                        new SimpleGrantedAuthority(
                            "ROLE_USER"
                        )
                    )
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id")
            .value(applicationId.toString()))
        .andExpect(jsonPath("$.status")
            .value("PENDING"))
        .andExpect(jsonPath("$.createdAt")
            .value("2026-08-03T12:00:00Z"));

    creditServiceMock.verify(
        1,
        postRequestedFor(
            urlEqualTo(CREDIT_SERVICE_PATH)
        )
            .withHeader(
                HttpHeaders.AUTHORIZATION,
                equalTo("Bearer test-token")
            )
    );
  }
}
