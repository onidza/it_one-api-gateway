package ru.creditbank.apigateway.auth.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import ru.creditbank.apigateway.auth.entity.Role;
import ru.creditbank.apigateway.config.property.JwtProperties;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

  private final JwtEncoder jwtEncoder;
  private final JwtProperties jwtProperties;
  private final SecureRandom secureRandom = new SecureRandom();

  /**
   * Generate JWT token using {@code MacAlgorithm.HS256} algorithm.
   * @param userId user id as UUID type
   * @param subject user email
   * @param role user role included as authority
   * @return JWT access token as string
   */
  public String generateAccessToken(
      UUID userId,
      String subject,
      Role role
  ) {
    Instant now = Instant.now();

    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer(jwtProperties.issuer())
        .subject(subject)
        .issuedAt(now)
        .expiresAt(now.plus(jwtProperties.accessTokenTtl()))
        .claim("user_id", userId.toString())
        .claim(
            "authorities",
            List.of("ROLE_" + role.name())
        )
        .build();

    log.debug("Generated access token for user, email = {}, role = {}",
        subject, role);

    JwsHeader header = JwsHeader
        .with(MacAlgorithm.HS256)
        .build();

    return jwtEncoder
        .encode(JwtEncoderParameters.from(header, claims))
        .getTokenValue();
  }

  public String generateRefreshToken() {
    byte[] bytes = new byte[32];
    secureRandom.nextBytes(bytes);

    log.debug("Generated refresh token");

    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(bytes);
  }
}
