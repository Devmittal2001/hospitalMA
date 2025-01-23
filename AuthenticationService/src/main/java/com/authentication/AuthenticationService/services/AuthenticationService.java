package com.authentication.AuthenticationService.services;

import com.authentication.AuthenticationService.dto.AuthenticationRequest;
import com.authentication.AuthenticationService.dto.RegisterRequest;
import com.authentication.AuthenticationService.exception.CustomException;
import com.authentication.AuthenticationService.models.Role;
import com.authentication.AuthenticationService.models.User;
import com.authentication.AuthenticationService.repositories.UserRepository;
import com.authentication.AuthenticationService.response.AuthenticationResponse;
import com.authentication.AuthenticationService.security.JwtService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest, Role registerRole) {
        logger.info("Registering new user with email: {}", registerRequest.getEmail());
        Optional<User> byEmail = userRepository.findByEmail(registerRequest.getEmail());
        if (!ObjectUtils.isEmpty(byEmail)) {
            throw new CustomException("user already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUserName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRole);
        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        String email = jwtService.getEmailFromToken(jwtToken);
        String username = jwtService.extractUsername(jwtToken);
        Role role = jwtService.getRoleFromToken(jwtToken);
        Date expTime = jwtService.getExpirationTimeFromToken(jwtToken);
        String refreshToken = jwtService.generateRefresh(new HashMap<>(), user);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setEmail(email);
        response.setUserName(username);
        response.setRole(role);
        response.setToken(jwtToken);
        response.setExpirationTime(expTime);

        logger.info("User registered successfully, JWT token generated for email: {}", email);
        return response;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        logger.info("Authenticating user with email: {}", authenticationRequest.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
        );

        var user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(() -> {
            logger.error("Authentication failed for email: {}", authenticationRequest.getEmail());
            return new IllegalArgumentException("Invalid email or password");
        });

        String jwtToken = jwtService.generateToken(user);
        String email = jwtService.getEmailFromToken(jwtToken);
        String username = jwtService.extractUsername(jwtToken);
        Role role = jwtService.getRoleFromToken(jwtToken);
        Date expTime = jwtService.getExpirationTimeFromToken(jwtToken);
        String refreshToken = jwtService.generateRefresh(new HashMap<>(), user);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setEmail(email);
        response.setUserName(username);
        response.setRole(role);
        response.setToken(jwtToken);
        response.setExpirationTime(expTime);

        logger.info("User authenticated successfully, JWT token generated for email: {}", email);
        return response;
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        logger.info("Refreshing token for refresh token: {}", refreshToken);

        var user = userRepository.findByEmail(jwtService.getEmailFromToken(refreshToken)).orElseThrow(() -> {
            logger.error("Invalid refresh token: {}", refreshToken);
            return new IllegalArgumentException("Invalid refresh token");
        });

        String jwtToken = jwtService.generateToken(user);
        String email = jwtService.getEmailFromToken(jwtToken);
        String username = jwtService.extractUsername(jwtToken);
        Role role = jwtService.getRoleFromToken(jwtToken);
        String newRefreshToken = jwtService.generateRefresh(new HashMap<>(), user);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setEmail(email);
        response.setUserName(username);
        response.setRole(role);
        response.setToken(jwtToken);

        logger.info("Token refreshed successfully for email: {}", email);
        return response;
    }

    public Boolean validateToken(String token) {
        logger.info("Validating token: {}", token);

        boolean isValid = jwtService.validateToken(token);

        if (isValid) {
            logger.info("Token is valid: {}", token);
        } else {
            logger.error("Token is invalid: {}", token);
        }

        return isValid;
    }
}
