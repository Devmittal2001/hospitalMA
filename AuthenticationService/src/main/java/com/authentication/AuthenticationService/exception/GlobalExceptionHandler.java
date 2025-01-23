package com.authentication.AuthenticationService.exception;

import com.authentication.AuthenticationService.response.ErrorResponse;
import com.authentication.AuthenticationService.util.Constants;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(HttpServletRequest request, org.springframework.security.access.AccessDeniedException e) throws org.springframework.security.access.AccessDeniedException {
        log.error("{} Throws AccessDeniedException : {}", request.getRequestURI(), e.getMessage());
        String requestUri = request.getRequestURI();
        if (requestUri.contains("/register/staff")) {
            return new ResponseEntity<>(new ErrorResponse(Constants.FAILURE, Constants.ADMIN_CAN_REGISTER), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(new ErrorResponse(Constants.FAILURE, Constants.ACCESS_DENIED), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<ErrorResponse> handleCustomException(HttpServletRequest request, CustomException ce) {
        String message = ce.getMessage();
        log.error("{} Throws CustomException : {}", request.getRequestURI(), message);
        return new ResponseEntity<>(new ErrorResponse(Constants.FAILURE, message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception ex) {
        String message = ex.getMessage();
        log.error("{} Throws Exception : {}", request.getRequestURI(), message);
        return new ResponseEntity<>(new ErrorResponse(Constants.FAILURE, message), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(HttpServletRequest request, RuntimeException ex) {
        String message = ex.getMessage() + " " + ex.getCause();
        log.error("{} Throws RuntimeException : {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Constants.FAILURE, message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialException(HttpServletRequest request, BadCredentialsException ex) {
        String message = ex.getMessage();
        log.error("{} Throws BadCredentialsException : {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Constants.FAILURE, Constants.INCORRECT_PASSWORD), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorResponse> handlePersistenceException(HttpServletRequest request, PersistenceException ex) {
        String message = ex.getMessage();
        log.error("{} Throws PersistenceException : {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Constants.FAILURE, message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(HttpServletRequest request, DataAccessException ex) {
        String message = ex.getMessage();
        log.error("{} Throws DataAccessException : {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Constants.FAILURE, message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(HttpServletRequest request, Exception e) {
        String message = e.getMessage();
        log.error("{} Throws ResourceNotFoundException : {}", request.getRequestURI(), e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Constants.FAILURE, message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(HttpServletRequest request, HttpClientErrorException.Unauthorized e) {
        String message = e.getMessage();
        log.error("{} Throws HttpClientErrorException.Unauthorized : {}", request.getRequestURI(), message);
        return new ResponseEntity<>(new ErrorResponse(Constants.FAILURE, message), HttpStatus.UNAUTHORIZED);
    }
}
