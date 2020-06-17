package com.reckue.account.handlers;

import com.reckue.account.exceptions.AlreadyExistsException;
import com.reckue.account.exceptions.AuthenticationException;
import com.reckue.account.exceptions.InvalidDataException;
import com.reckue.account.exceptions.NotFoundException;
import com.reckue.account.transfers.ErrorTransfer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return new ResponseEntity<>(new ErrorTransfer(e.getMessage(), e.getHttpStatus().value()), e.getHttpStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(new ErrorTransfer(e.getMessage(), e.getHttpStatus().value()), e.getHttpStatus());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<?> handleAlreadyExistsException(AlreadyExistsException e) {
        return new ResponseEntity<>(new ErrorTransfer(e.getMessage(), e.getHttpStatus().value()), e.getHttpStatus());
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<?> handleInvalidDataException(InvalidDataException e) {
        return new ResponseEntity<>(new ErrorTransfer(e.getMessage(), e.getHttpStatus().value()), e.getHttpStatus());
    }
}
