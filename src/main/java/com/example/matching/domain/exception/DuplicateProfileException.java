package com.example.matching.domain.exception;

public class DuplicateProfileException extends RuntimeException {
    public DuplicateProfileException() {
        super("プロフィールはすでに作成済みです");
    }

    public DuplicateProfileException(Long userId) {
        super("プロフィールはすでに作成済みです: userId=" + userId);
    }
}
