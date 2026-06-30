package com.example.matching.presentation.dto;

public record ProfileResponse(
        Long id,
        Long userId,
        String gender,
        int age,
        String prefecture,
        String occupation,
        Integer income,
        Integer height,
        String education,
        String bodyType,
        String selfIntroduction,
        String hobbies,
        String lifestyle,
        String relationshipHistory
) {}
