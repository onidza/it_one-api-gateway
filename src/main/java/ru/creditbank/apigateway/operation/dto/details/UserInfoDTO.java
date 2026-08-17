package ru.creditbank.apigateway.operation.dto.details;

public record UserInfoDTO(
    String userId,
    String fullName,
    String email
) {

}
