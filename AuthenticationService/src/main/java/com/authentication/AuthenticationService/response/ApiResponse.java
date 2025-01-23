package com.authentication.AuthenticationService.response;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ApiResponse {

    private String status;
    private String message;
    private Object data;

    public ApiResponse(String status, String message, Object data) {
        super();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    public ApiResponse(String status, Object object) {
        super();
        this.status = status;
        this.data = object;
    }

    private ApiResponse() {
        super();
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
