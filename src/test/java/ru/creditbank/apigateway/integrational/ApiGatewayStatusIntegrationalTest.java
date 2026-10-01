package ru.creditbank.apigateway.integrational;

import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.creditbank.apigateway.config.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ApiGatewayStatusIntegrationalTest {

  private static final String GATEWAY_DETAILS_PATH =
      "/credit-service/api/v1/credit/{id}";

  private static final String GATEWAY_STATUS_PATH =
      "/credit-service/api/v1/credit/{id}/status";

  @RegisterExtension
  static WireMockExtension creditServiceMock =
      WireMockExtension.newInstance()
          .options(
              wireMockConfig()
                  .dynamicPort()
                  .http2PlainDisabled(true)
          )
          .build();

  @Autowired
  private MockMvc mockMvc;

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "rest.credit-service.url",
        creditServiceMock::baseUrl
    );
  }

  @Test
  void shouldReturnForbiddenWhenUserWithoutCreditManagerRoleRequestsDetails() throws Exception {
    UUID applicationId = UUID.randomUUID();

    mockMvc.perform(
            get(GATEWAY_DETAILS_PATH, applicationId)
                .with(jwt()
                    .authorities(
                        new SimpleGrantedAuthority("ROLE_USER")
                    )
                )
        )
        .andDo(print())
        .andExpect(status().isForbidden());

    creditServiceMock.verify(
        0,
        anyRequestedFor(anyUrl())
    );
  }

  @Test
  void shouldReturnForbiddenWhenUserWithoutCreditManagerRoleChangesStatus() throws Exception {
    UUID applicationId = UUID.randomUUID();

    String requestBody = """
        {
          "status": "APPROVED",
          "managerComment": "Заявка одобрена",
          "interestRate": 15.30
        }
        """;

    mockMvc.perform(
            patch(GATEWAY_STATUS_PATH, applicationId)
                .with(jwt()
                    .authorities(
                        new SimpleGrantedAuthority("ROLE_USER")
                    )
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
        .andDo(print())
        .andExpect(status().isForbidden());

    creditServiceMock.verify(
        0,
        anyRequestedFor(anyUrl())
    );
  }
}
