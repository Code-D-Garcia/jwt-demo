package com.codedgarcia.jwt_demo.auth.service;

import com.codedgarcia.jwt_demo.auth.dto.AuthResponse;
import com.codedgarcia.jwt_demo.auth.dto.LoginRequest;
import com.codedgarcia.jwt_demo.auth.dto.RefreshRequest;
import com.codedgarcia.jwt_demo.auth.dto.RegisterRequest;
import com.codedgarcia.jwt_demo.user.entity.User;
import com.codedgarcia.jwt_demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse authLogin(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                 loginRequest.username(),
                 loginRequest.password()));

        UserDetails user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        String token = jwtService.getToken(user);
        String refreshToken = jwtService.getRefreshToken(user);
        return new AuthResponse(token, refreshToken);

    }

    public AuthResponse authRegister(RegisterRequest registerRequest) {
        if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        User user = User.builder()
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .build();
        userRepository.save(user);
        return new AuthResponse(jwtService.getToken(user), jwtService.getRefreshToken(user));
    }

    public AuthResponse authRefreshToken(RefreshRequest request) {
        String username = jwtService.getUsernameFromToken(request.refreshToken());
        UserDetails user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        if (!jwtService.isTokenValid(request.refreshToken(), user)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String newAccessToken = jwtService.getToken(user);
        String newRefreshToken = jwtService.getRefreshToken(user);
        return  new AuthResponse(newAccessToken, newRefreshToken);
    }
}