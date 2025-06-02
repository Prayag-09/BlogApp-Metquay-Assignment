package com.example.Metquay.controller;

import com.example.Metquay.dtos.APIError;
import com.example.Metquay.dtos.AuthResponse;
import com.example.Metquay.dtos.LoginRequest;
import com.example.Metquay.dtos.RegisterRequest;
import com.example.Metquay.services.Auth.Auth;
import com.example.Metquay.services.User.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author prayagtushar
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final Auth authenticationService;
    private final UserService userService;

    @Value("${jwt.expiry.ms:86400000}")
    private long jwtExpiryMs;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            log.info("Registering user with email: {}", registerRequest.getEmail());
            userService.registerUser(registerRequest);
            log.info("User saved successfully, proceeding to authenticate");
            UserDetails user = authenticationService.authenticate(
                    registerRequest.getEmail(),
                    registerRequest.getPassword()
            );
            log.info("User authenticated, generating token");
            AuthResponse response = AuthResponse.builder()
                    .token(authenticationService.generateToken(user))
                    .expireIn(jwtExpiryMs / 1000)
                    .build();
            log.info("Token generated, returning response");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (EntityExistsException e) {
            log.warn("Duplicate email detected: {}", registerRequest.getEmail());
            APIError error = APIError.builder()
                    .status(HttpStatus.CONFLICT.value())
                    .message("User with email " + registerRequest.getEmail() + " already exists")
                    .errors(List.of())
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Logging in user with email: {}", loginRequest.getEmail());
        UserDetails user = authenticationService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
        log.info("User authenticated, generating token");
        AuthResponse response = AuthResponse.builder()
                .token(authenticationService.generateToken(user))
                .expireIn(jwtExpiryMs / 1000)
                .build();
        log.info("Token generated, returning response");
        return ResponseEntity.ok(response);
    }
}