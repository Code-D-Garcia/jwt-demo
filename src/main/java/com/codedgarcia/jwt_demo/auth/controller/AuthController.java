package com.codedgarcia.jwt_demo.auth.controller;

import com.codedgarcia.jwt_demo.auth.dto.AuthResponse;
import com.codedgarcia.jwt_demo.auth.dto.LoginRequest;
import com.codedgarcia.jwt_demo.auth.dto.RefreshRequest;
import com.codedgarcia.jwt_demo.auth.dto.RegisterRequest;
import com.codedgarcia.jwt_demo.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authLogin(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.authRegister(registerRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.authRefreshToken(refreshRequest));
    }
}
