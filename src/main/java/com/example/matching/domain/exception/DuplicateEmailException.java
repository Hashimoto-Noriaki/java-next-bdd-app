package com.example.matching.domain.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("このメールアドレスはすでに使われています: " + email);
    }
}
