package com.example.matching.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("ユーザーが見つかりません: userId=" + id);
    }

    public UserNotFoundException(String email) {
        super("ユーザーが見つかりません");
    }
}
