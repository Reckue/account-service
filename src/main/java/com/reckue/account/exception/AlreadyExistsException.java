package com.reckue.account.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Class AlreadyExistsException is responsible for throwing
 * exception when the created model already exists in database.
 *
 * @author Kamila Meshcheryakova
 */
@Getter
@Setter
public class AlreadyExistsException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    /**
     * Constructor with passed parameters as an information about the exception.
     *
     * @param message    information about exception
     * @param httpStatus the description about Http status code
     */
    public AlreadyExistsException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
