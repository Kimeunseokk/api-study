package com.example.demo.domain.user.repository;

import com.example.demo.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email); //
    boolean existsByNickname(String nickname);
    boolean existsByPhone(String phone);
    boolean existsByUsername(String username);
    boolean existsByPassword(String password);

}
