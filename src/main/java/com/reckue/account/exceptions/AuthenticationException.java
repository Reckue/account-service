package com.reckue.account.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AuthenticationException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    public AuthenticationException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
