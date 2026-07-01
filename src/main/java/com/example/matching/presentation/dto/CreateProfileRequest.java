package com.example.matching.presentation.dto;

import com.example.matching.domain.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProfileRequest(
        @NotNull(message = "性別を選択してください") Gender gender,
        @NotNull(message = "年齢を入力してください") @Min(value = 18, message = "18歳以上の方のみご利用いただけます") @Max(100) Integer age,
        @NotBlank(message = "住まいを選択してください") String prefecture,
        String occupation,
        Integer income,
        Integer height,
        String education,
        String bodyType,
        @Size(max = 500, message = "自己紹介文は500文字以内で入力してください") String selfIntroduction,
        String hobbies,
        String lifestyle,
        String relationshipHistory
) {}
