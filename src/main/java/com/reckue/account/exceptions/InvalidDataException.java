package com.reckue.account.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Class InvalidDataException is responsible for throwing
 * exception when the requested data is incorrect.
 *
 * @author Kamila Meshcheryakova
 */
@Getter
@Setter
public class InvalidDataException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    /**
     * Constructor with passed parameters as an information about the exception.
     *
     * @param message    information about exception
     * @param httpStatus the description about Http status code
     */
    public InvalidDataException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

