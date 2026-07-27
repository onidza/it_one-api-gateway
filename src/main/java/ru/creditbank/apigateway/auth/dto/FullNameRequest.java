package ru.creditbank.apigateway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FullNameRequest(

        @JsonProperty("firstname")
        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        String firstName,

        @JsonProperty("lastname")
        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        String lastName,

        @JsonProperty("middlename")
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        String middleName
) {}
