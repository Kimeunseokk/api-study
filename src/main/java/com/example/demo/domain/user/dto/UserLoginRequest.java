package com.example.demo.domain.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
public class UserLoginRequest {
    private String email;
    private String password;
}
