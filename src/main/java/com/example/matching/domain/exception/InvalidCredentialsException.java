package com.example.matching.domain.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("メールアドレスまたはパスワードが正しくありません");
    }
}
