package org.starcoin.starswap.api.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e)
    {
        ApiError error = new ApiError();
        error.setException("Illegal argument exception: " + e.getMessage());
        LOGGER.error(error.getException(), e);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<ApiError>(error, status);
    }

    @ExceptionHandler(value = ArithmeticException.class)
    public ResponseEntity<ApiError> handleArithmeticException(ArithmeticException e)
    {
        ApiError error = new ApiError();
        String uuid = UUID.randomUUID().toString();
        error.setException("Arithmetic exception: " + uuid);
        LOGGER.error(error.getException(), e);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<ApiError>(error, status);
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointerException(NullPointerException e)
    {
        ApiError error = new ApiError();
        String uuid = UUID.randomUUID().toString();
        error.setException("Null pointer exception: " + uuid);
        LOGGER.error(error.getException(), e);
        HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
        return new ResponseEntity<ApiError>(error, status);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e)
    {
        ApiError error = new ApiError();
        String uuid = UUID.randomUUID().toString();
        error.setException("Exception: " + uuid);
        LOGGER.error(error.getException(), e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<ApiError>(error, status);
    }
}