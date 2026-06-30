package com.example.matching.presentation.dto;

import com.example.matching.domain.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProfileRequest(
        @NotNull Gender gender,
        @NotNull @Min(18) @Max(100) Integer age,
        @NotBlank String prefecture,
        String occupation,
        Integer income,
        Integer height,
        String education,
        String bodyType,
        @Size(max = 500) String selfIntroduction,
        String hobbies,
        String lifestyle,
        String relationshipHistory
) {}
