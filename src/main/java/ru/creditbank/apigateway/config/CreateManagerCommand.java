package ru.creditbank.apigateway.config;

import lombok.RequiredArgsConstructor;
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

  @Bean
  CommandLineRunner createCreditManager() {
    return args -> {
      String email = "manager@mail.ru";

      if (!userRepository.existsByEmail(email)) {
        UserEntity manager = new UserEntity(
            email,
            passwordEncoder.encode("Manager123"),
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