package com.example.demo.security;

import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    public String createToken(Long userId){
        return "jwt-token-for-user" + userId;
    }
}
