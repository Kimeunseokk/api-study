package com.example.demo.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class JwtToken {
    private String grantType;
    private String refreshToken;
    private String accessToken;
}
