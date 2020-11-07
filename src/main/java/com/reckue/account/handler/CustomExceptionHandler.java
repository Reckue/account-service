package com.reckue.account.handler;

import com.reckue.account.exception.*;
import com.reckue.account.transfer.ErrorTransfer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Class CustomExceptionHandler allows to handle all exceptions.
 *
 * @author Kamila Meshcheryakova
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * This method is used to handle an AuthenticationException.
     *
     * @param e AuthenticationException
     * @return an exception response in the form of an object ErrorTransfer class
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return new ResponseEntity<>(new ErrorTransfer(e.getMessage(), HttpStatus.UNAUTHORIZED,
                HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * This method is used to handle an AccessDeniedException.
     *
     * @param e AuthenticationException
     * @return an exception response in the form of an object ErrorTransfer class
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(new ErrorTransfer(e.getMessage(), HttpStatus.FORBIDDEN,
                HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
    }

    /**
     * This method is used to handle a NotFoundException.
     *
     * @param e NotFoundException
     * @return an exception response in the form of an object ErrorTransfer class
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(new ErrorTransfer(e.getMessage(), HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    /**
     * This method is used to handle an AlreadyExistsException.
     *
     * @param e AlreadyExistsException
     * @return an exception response in the form of an object ErrorTransfer class
     */
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<?> handleAlreadyExistsException(AlreadyExistsException e) {
        return new ResponseEntity<>(new ErrorTransfer(e.getMessage(), HttpStatus.CONFLICT, HttpStatus.CONFLICT.value()),
                HttpStatus.CONFLICT);
    }

    /**
     * This method is used to handle an InvalidDataException.
     *
     * @param e InvalidDataException
     * @return an exception response in the form of an object ErrorTransfer class
     */
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<?> handleInvalidDataException(InvalidDataException e) {
        return new ResponseEntity<>(new ErrorTransfer(e.getMessage(), HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }
}
