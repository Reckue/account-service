package com.reckue.account.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Class AccessDeniedException is responsible for throwing
 * exception when the requested data contains authentication errors connecting with access.
 *
 * @author Kamila Meshcheryakova
 */
@Getter
@Setter
public class AccessDeniedException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    /**
     * Constructor with passed parameters as an information about the exception.
     *
     * @param message    information about exception
     * @param httpStatus the description about Http status code
     */
    public AccessDeniedException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
