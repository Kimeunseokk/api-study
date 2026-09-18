package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.entity.Role;
import com.example.demo.domain.user.entity.Users;
import com.example.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping
    public String adminPage(Principal principal, Model model) {
        List<Users> users = userService.findAllUsers();
        model.addAttribute("pageName", "관리자 페이지");
        model.addAttribute("users", users);
        model.addAttribute("currentEmail", principal.getName());
        return "admin";
    }

    @PostMapping("/role/{userId}")
    public String updateRole(@PathVariable Long userId,
                             @RequestParam Role role) {
        userService.updateRole(userId, role);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return "redirect:/admin";
    }
}