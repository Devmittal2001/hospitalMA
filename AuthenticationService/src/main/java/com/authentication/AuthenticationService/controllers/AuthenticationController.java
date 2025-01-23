package com.authentication.AuthenticationService.controllers;

import com.authentication.AuthenticationService.dto.AuthenticationRequest;
import com.authentication.AuthenticationService.dto.RegisterRequest;
import com.authentication.AuthenticationService.models.Role;
import com.authentication.AuthenticationService.response.ApiResponse;
import com.authentication.AuthenticationService.response.AuthenticationResponse;
import com.authentication.AuthenticationService.services.AuthenticationService;
import com.authentication.AuthenticationService.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth-service")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse> registerAdmin(@RequestBody RegisterRequest registerRequest) {
        AuthenticationResponse res = authenticationService.register(registerRequest, Role.ADMIN);
        logger.info("Admin registered successfully: {}", registerRequest.getEmail());
        return new ResponseEntity<>(new ApiResponse(Constants.SUCCESS, "Admin registered successfully", res), HttpStatus.OK);

    }

    @PostMapping("/register/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> registerStaff(@RequestBody RegisterRequest registerRequest) {
        AuthenticationResponse res = authenticationService.register(registerRequest, Role.STAFF);
        logger.info("Staff registered successfully: {}", registerRequest.getEmail());
        return new ResponseEntity<>(new ApiResponse(Constants.SUCCESS, "Staff registered successfully", res), HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        logger.info("User authenticated successfully: {}", authenticationRequest.getEmail());
        AuthenticationResponse res = authenticationService.authenticate(authenticationRequest);
        return new ResponseEntity<>(new ApiResponse(Constants.SUCCESS, "User authenticated successfully.", res), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(@RequestParam("token") String refreshToken) {
        logger.info("Refresh token successful.");
        AuthenticationResponse res = authenticationService.refreshToken(refreshToken);
        return new ResponseEntity<>(new ApiResponse(Constants.SUCCESS, "Refresh token successful.", res), HttpStatus.OK);

    }

    @GetMapping("/validateToken")
    public ResponseEntity<ApiResponse> validateToken(@RequestParam("token") String token) {
        Boolean res = authenticationService.validateToken(token);
        logger.info("Token validation result: {}", res);
        return new ResponseEntity<>(new ApiResponse(Constants.SUCCESS, "Token is validated successfully", res), HttpStatus.OK);
    }
}
