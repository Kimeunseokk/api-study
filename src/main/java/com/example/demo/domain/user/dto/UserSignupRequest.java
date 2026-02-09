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
                this.username,   // 1. username
                encodedPassword, // 2. password
                this.email,      // 3. email
                this.phone,      // 4. phone
                this.nickname    // 5. nickname
        );
    }
}
