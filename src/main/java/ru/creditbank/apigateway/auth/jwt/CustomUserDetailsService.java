package ru.creditbank.apigateway.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.creditbank.apigateway.auth.entity.UserEntity;
import ru.creditbank.apigateway.auth.repository.UserRepository;

/**
 * Detail service config for correct Spring security user details provider working.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * @param email the username identifying the user whose data is required.
   */
  @Override
  public UserDetails loadUserByUsername(String email) {
    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() ->
            new UsernameNotFoundException("Invalid email or password")
        );

    return User.builder()
        .username(user.getEmail())
        .password(user.getPasswordHash())
        .roles(user.getRole().name())
        .build();
  }
}