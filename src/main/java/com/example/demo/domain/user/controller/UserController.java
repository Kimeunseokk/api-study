package com.example.demo.domain.user.controller;


import com.example.demo.domain.user.dto.UserLoginRequest;
import com.example.demo.domain.user.dto.UserMeResponse;
import com.example.demo.domain.user.dto.UserSignupRequest;
import com.example.demo.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserSignupRequest signup(@RequestBody UserSignupRequest userSignupRequest) {
        return userSignupRequest;
    }

    @PostMapping("/login") // 로그인
    public UserLoginRequest login(@RequestBody UserLoginRequest userLoginRequest) {
        return userLoginRequest;
    }

//    @GetMapping("/me")
//    public UserMeResponse me() {
//
//    }


}
