package com.reckue.account.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Class NotFoundException is responsible for throwing
 * exception when the received model is not found in the database.
 *
 * @author Kamila Meshcheryakova
 */
@Getter
@Setter
public class NotFoundException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    /**
     * Constructor with passed parameters as an information about the exception.
     *
     * @param message    information about exception
     * @param httpStatus the description about Http status code
     */
    public NotFoundException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
