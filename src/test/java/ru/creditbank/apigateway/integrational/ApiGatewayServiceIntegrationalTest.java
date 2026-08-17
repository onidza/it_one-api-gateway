package ru.creditbank.apigateway.integrational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.apigateway.auth.dto.register.FullNameDTO;
import ru.creditbank.apigateway.auth.dto.register.RegisterRequestDTO;
import ru.creditbank.apigateway.auth.entity.Role;
import ru.creditbank.apigateway.auth.entity.UserEntity;
import ru.creditbank.apigateway.auth.repository.UserRepository;
import ru.creditbank.apigateway.auth.service.AuthService;
import ru.creditbank.apigateway.config.TestcontainersConfiguration;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class ApiGatewayServiceIntegrationalTest {

  @Autowired
  private AuthService authService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  void shouldSaveNewUserRegistration() {
    RegisterRequestDTO request = new RegisterRequestDTO(
        "example@mail.ru",
        new FullNameDTO(
            "Иван",
            "Иванов",
            "Иванович"
        ),
        "Credit123"
    );

    authService.register(request);

    UserEntity user = userRepository.findByEmail("example@mail.ru")
        .orElseThrow();

    assertEquals(request.email(), user.getEmail());
    assertEquals(request.fullName().firstName(), user.getFirstName());
    assertEquals(request.fullName().lastName(), user.getLastName());
    assertEquals(request.fullName().middleName(), user.getMiddleName());
    assertEquals(Role.USER, user.getRole());
    assertNotNull(user.getId());

    assertTrue(
        passwordEncoder.matches(
            request.password(),
            user.getPasswordHash()
        )
    );
  }
}