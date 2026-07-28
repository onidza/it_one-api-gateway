package ru.creditbank.apigateway.auth.mapper;

import org.springframework.stereotype.Component;
import ru.creditbank.apigateway.auth.dto.RegisterRequestDTO;
import ru.creditbank.apigateway.auth.entity.UserEntity;

import java.util.Objects;

@Component
public class UserMapper {

    public UserEntity toEntity(
            RegisterRequestDTO request,
            String passwordHash
    ) {
        Objects.requireNonNull(request, "request can't be null");

        return new UserEntity(
                request.email(),
                passwordHash,
                request.fullName().firstName(),
                request.fullName().lastName(),
                request.fullName().middleName()
        );
    }
}
