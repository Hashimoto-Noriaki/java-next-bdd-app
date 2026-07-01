package com.example.matching.domain.exception;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(Long userId) {
        super("プロフィールが見つかりません: userId=" + userId);
    }

    public ProfileNotFoundException(String message) {
        super(message);
    }
}
