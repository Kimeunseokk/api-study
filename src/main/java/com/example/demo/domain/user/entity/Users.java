package com.example.demo.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private String phone;
    @Column
    private String RefreshToken;
    @Column
    private String role;
    @Column
    private String nickname;

    public Users(String username, String password,  String email, String phone, String nickname){
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
    }

}
