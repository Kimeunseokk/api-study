package com.example.demo.domain.user.service;

import com.example.demo.config.SecurityConfig;
import com.example.demo.security.JwtProvider;
import com.example.demo.domain.user.dto.UserLoginRequest;
import com.example.demo.domain.user.dto.UserLoginResponse;
import com.example.demo.domain.user.dto.UserMeResponse;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;
    private final PasswordEncoder  passwordEncoder;
    private final JwtProvider jwtprovider;

    @Transactional // 로그인
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.getEmail())
                .orElseThrow(()->new IllegalArgumentException());
        if(!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())){
            throw new IllegalArgumentException();
        }

        String token = jwtprovider.createToken(user.getId());
        return new UserLoginResponse(token);
    }

//    @Transactional // 정보수정
//    public UserMeResponse login(String email, String password) {
//
//    }

}
