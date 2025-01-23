package com.authentication.AuthenticationService.response;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ErrorResponse {

    private String status;

    public ErrorResponse() {
    }

    public ErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;
}