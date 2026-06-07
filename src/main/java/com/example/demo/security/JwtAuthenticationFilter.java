package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;

        // 1. 브라우저가 보낸 쿠키들 중에서 우리가 구운 "jwtToken"을 찾습니다.
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 2. 토큰이 발견되었고, 그 토큰이 유효한지(위조되지 않았는지, 만료되지 않았는지) 검사합니다.
        if (token != null && jwtProvider.validateToken(token)) {
            // 토큰이 정상이라면, 토큰에서 유저 정보를 꺼내 스프링 시큐리티에 "이 사람 로그인 된 사람이야!" 라고 등록해 줍니다.
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 검사가 끝났으면 다음 단계(다음 필터나 컨트롤러)로 요청을 자연스럽게 넘겨줍니다.
        filterChain.doFilter(request, response);
    }
}