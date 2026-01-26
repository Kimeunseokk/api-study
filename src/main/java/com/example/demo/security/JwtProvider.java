package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;


import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 토큰 유효시간 (1시간)
    private final long vaildityInMillisseconds = 60 * 60 * 1000;

    // 토큰 생성
    public String createToken(String role, String email) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("Role", role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + vaildityInMillisseconds);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiry)
                .setIssuedAt(now)
                .signWith(secretKey)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

