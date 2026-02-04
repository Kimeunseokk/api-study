package com.example.demo.domain.user.controller;


import com.example.demo.domain.user.dto.UserLoginRequest;
import com.example.demo.domain.user.dto.UserMeResponse;
import com.example.demo.domain.user.dto.UserSignupRequest;
import com.example.demo.domain.user.entity.Users;
import com.example.demo.domain.user.service.CustomUserDatailsService;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.security.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtprovider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDatailsService userDatailsService;


    @GetMapping
    public ResponseEntity<?> home(Long userId){
        Users loginUser = userService.getLoginUserById(userId);
        if(loginUser == null) {
            return ResponseEntity.ok().body(
                    Map.of(
                            "login", false,
                            "loginType", "cookie-login"
                    )
            );
        }
        return ResponseEntity.ok().body(
                Map.of(
                        "login", true,
                        "loginType", "cookie-login",
                        "nickname", loginUser.getNickname()
                )
        );
    }

    @PostMapping("/signup") // 회원가입
    public ResponseEntity<?> signup(@RequestBody UserSignupRequest userSignupRequest) {
        userService.signup(userSignupRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login") // 로그인
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword())
        );
        User user = (User) authentication.getPrincipal(); // org.springframework.security.core.userdetails.User
        String token = jwtprovider.createToken(user.getUsername(), user.getAuthorities().iterator().next().getAuthority());
        return ResponseEntity.ok().body(token);
    }

//    @GetMapping("/me")
//    public UserMeResponse me() {
//
//    }


}
