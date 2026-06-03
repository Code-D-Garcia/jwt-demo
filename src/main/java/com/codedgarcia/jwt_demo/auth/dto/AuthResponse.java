package com.codedgarcia.jwt_demo.auth.dto;



public record AuthResponse(
        String accessToken,
        String refreshToken
    ) {

}
