package com.authentication.AuthenticationService.response;

import com.authentication.AuthenticationService.models.Role;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserResponse {
    private String email;
    private String userName;
    private String token;
    private Timestamp expirationTime;
    private Role role;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
