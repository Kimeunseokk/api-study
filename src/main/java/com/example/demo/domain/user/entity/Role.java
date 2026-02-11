package com.example.demo.domain.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum Role {
    // 1. 상수 정의 (이름, 설명)
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private String role;
    private String description;
    Role(String role, String description) {
        this.role = role;
        this.description = description;
    }
}
