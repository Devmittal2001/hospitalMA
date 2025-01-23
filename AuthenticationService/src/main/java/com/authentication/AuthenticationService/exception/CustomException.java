package com.authentication.AuthenticationService.exception;

import org.springframework.validation.ObjectError;

import java.util.Iterator;


public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String message;
    public Object errors;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Object errors) {
        super(message);
        this.errors = errors;
    }

    public CustomException(Iterator<ObjectError> iterator) {
        this.errors = iterator;
    }

}
