package ru.creditbank.apigateway.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.creditbank.apigateway.auth.entity.UserEntity;

@Service
@Slf4j
public class JwtService {


    public String generateToken(Authentication authentication) {
    }
}
