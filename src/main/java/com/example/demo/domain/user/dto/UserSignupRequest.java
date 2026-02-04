package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.entity.Users;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserSignupRequest {
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String phone;

    public Users toEntity(String encodedPassword) {
        return new Users(
                username,
                email,
                encodedPassword,
                nickname,
                phone
        );
    }
}
