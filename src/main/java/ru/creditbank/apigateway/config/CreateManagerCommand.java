package ru.creditbank.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.creditbank.apigateway.auth.entity.Role;
import ru.creditbank.apigateway.auth.entity.UserEntity;
import ru.creditbank.apigateway.auth.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class CreateManagerCommand {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${test.credit-manager.email}")
  private String email;

  @Value("${test.credit-manager.password}")
  private String password;

  @Bean
  CommandLineRunner createCreditManager() {
    return args -> {
      if (!userRepository.existsByEmail(this.email)) {
        UserEntity manager = new UserEntity(
            this.email,
            passwordEncoder.encode(this.password),
            "Credit",
            "Manager",
            "Manager"
        );

        manager.changeRole(Role.CREDIT_MANAGER);
        userRepository.save(manager);
      }
    };
  }
}