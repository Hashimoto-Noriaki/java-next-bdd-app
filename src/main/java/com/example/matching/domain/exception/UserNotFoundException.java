package com.example.matching.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("ユーザーが見つかりません: " + id);
    }

    public UserNotFoundException(String email) {
        super("ユーザーが見つかりません: " + email);
    }
}
