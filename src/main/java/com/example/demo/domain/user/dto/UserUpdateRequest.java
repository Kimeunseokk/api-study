package com.example.demo.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String nickname;
    private String phone;
    private String password;
    private String passwordCheck;
}
