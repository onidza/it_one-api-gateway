package ru.creditbank.apigateway.auth.dto.register;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FullNameDTO(

    @Schema(description = "Имя", example = "Ivan")
    @JsonProperty("firstname")
    @NotBlank(message = "First name is required")
    @Size(
        min = 2,
        max = 50,
        message = "First name must be between 2 and 50 characters"
    )
    @Pattern(
        regexp = "^[А-Яа-яЁёA-Za-z-]+$",
        message = "First name contains invalid characters"
    )
    String firstName,

    @Schema(description = "Фамилия", example = "Stupnitsky")
    @JsonProperty("lastname")
    @NotBlank(message = "Last name is required")
    @Size(
        min = 2,
        max = 50,
        message = "Last name must be between 2 and 50 characters"
    )
    @Pattern(
        regexp = "^[А-Яа-яЁёA-Za-z-]+$",
        message = "Last name contains invalid characters"
    )
    String lastName,

    @Schema(description = "Отчество", example = "Vladimirovich")
    @JsonProperty("middlename")
    @Size(
        min = 2,
        max = 50,
        message = "Middle name must be between 2 and 50 characters"
    )
    @Pattern(
        regexp = "^[А-Яа-яЁёA-Za-z-]+$",
        message = "Middle name contains invalid characters"
    )
    String middleName
) {

}