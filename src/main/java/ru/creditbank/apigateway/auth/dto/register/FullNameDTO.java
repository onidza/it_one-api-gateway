package ru.creditbank.apigateway.auth.dto.register;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FullNameDTO(

    @Schema(description = "Имя", example = "Ivan")
    @JsonProperty("firstname")
    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
    String firstName,

    @Schema(description = "Фамилия", example = "Stupnitsky")
    @JsonProperty("lastname")
    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
    String lastName,

    @Schema(description = "Отчество", example = "Vladimirovich")
    @JsonProperty("middlename")
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
    String middleName
) {

}