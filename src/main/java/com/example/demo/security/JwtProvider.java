package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 토큰 유효시간 (1시간)
    private final long vaildityInMillisseconds = 60 * 60 * 1000;

    // 💡 수정됨: 파라미터 순서를 email, role 순서로 변경했습니다 (컨트롤러와 맞춤)
    public String createToken(String email, String role) {
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

    // 💡 추가됨: 토큰을 열어서 권한(Authentication) 정보를 시큐리티에게 넘겨주는 메서드
    public Authentication getAuthentication(String token) {
        // 1. 토큰에서 데이터(Claims) 열기
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

        // 2. 이메일(Subject)과 권한(Role) 꺼내기
        String email = claims.getSubject();
        String role = claims.get("Role", String.class);

        // 3. 스프링 시큐리티가 읽을 수 있는 권한 형태로 변환
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

        // 4. 시큐리티용 가짜 유저 객체(Principal) 만들기 (비밀번호는 이미 인증됐으니 비워둠)
        User principal = new User(email, "", authorities);

        // 5. 최종 인증 객체 리턴
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}