package com.example.demo.domain.user.service;

import com.example.demo.domain.user.entity.Users;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDatailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + email)
                );

        // role이 null일 경우를 대비해 기본값 설정


        return User.builder()
                .username(users.getEmail())   // 로그인 기준
                .password(users.getPassword())
                .roles(String.valueOf(users.getRole()))       // "USER", "ADMIN"
                .build();
    }
}
