package com.ruiz.prestamos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestException.class)
    public ResponseEntity<ErrorDTO> requestException(RequestException ex) {
        return new ResponseEntity<>(ex.getErrorDTO(), HttpStatus.BAD_REQUEST);
    }
}