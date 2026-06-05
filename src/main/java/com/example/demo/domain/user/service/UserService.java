package com.example.demo.domain.user.service;

import com.example.demo.config.SecurityConfig;
import com.example.demo.domain.user.dto.UserLoginRequest;
import com.example.demo.domain.user.dto.UserSignupRequest;
import com.example.demo.domain.user.entity.Users;
import com.example.demo.security.JwtProvider;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;
    private final JwtProvider jwtprovider;
    private final AuthenticationManager authenticationManager;

    // Controller로부터 로그인 요청을 받아 인증 후 최종 완성된 '토큰'만 돌려줍니다.
    public String authenticateAndGenerateToken(UserLoginRequest request) {
        // 1. 시큐리티 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. 인증 성공 시 토큰 생성 및 반환
        User user = (User) authentication.getPrincipal();
        return jwtprovider.createToken(user.getUsername(), user.getAuthorities().iterator().next().getAuthority());
    }

    public boolean checkEmaildouplication(String email) {
        return userRepository.existsByEmail(email);
    } // 회원가입시 중복 이메일 확인

    public boolean checkNicknamedouplication(String nickname) {
        return userRepository.existsByNickname(nickname);
    } // 회원가입시 중복 닉네임 확인

    public boolean checkPhoneduplication(String phone) {
        return userRepository.existsByPhone(phone);
    } // 회원가입시 전화번호 중복 확인

    public boolean checkUsernamedouplication(String username) {
        return userRepository.existsByUsername(username);
    } // 회원가입시

    @Transactional
    public Users getLoginUserById(Long userId) {
        if(userId == null) return null;

        Optional<Users> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }

    @Transactional
    public void signup(UserSignupRequest Request) {
        if(checkEmaildouplication(Request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        } // 이메일 중복 체크

        if(checkNicknamedouplication(Request.getNickname())) {
            throw new IllegalArgumentException("Nickname already exists");
        } // 닉네임 중복 체크

        if(checkPhoneduplication(Request.getPhone())) {
            throw new IllegalArgumentException("Phone already exists");
        } // 전화번호 중복 체크

        if(checkUsernamedouplication(Request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        } // 유저닉네임 중복 체크

        System.out.println("회원가입 요청 데이터: " + Request.getEmail());
        String encode = passwordEncoder.encode(Request.getPassword());
        Users savedUser = userRepository.save(Request.toEntity(encode));
        System.out.println("저장된 유저 ID: " + savedUser.getId());
    }

    @Transactional
    public void checkinfo(Long userId){

    }




}
