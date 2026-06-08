package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.UserLoginRequest;
import com.example.demo.domain.user.dto.UserSignupRequest;
import com.example.demo.domain.user.dto.UserUpdateRequest;
import com.example.demo.domain.user.entity.Users;
import com.example.demo.domain.user.service.CustomUserDatailsService;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.security.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException; // 💡 에러 처리를 위해 추가됨
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller // HTML 화면 렌더링을 위한 컨트롤러
@RequestMapping("/{loginType}") // HTML 폼 주소 구조(/{loginType}/...)와 매핑
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtprovider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDatailsService userDatailsService;

    // 1. 홈 화면 열기
    @GetMapping("/home")
    public String home(@PathVariable String loginType, Principal principal, Model model) {
        model.addAttribute("loginType", loginType);
        model.addAttribute("pageName", "홈 화면");

        if (principal != null) {
            Users loginUser = userService.findByEmail(principal.getName());

            // 🚨 방어 코드 추가! (만약 토큰은 있는데 DB에서 유저가 삭제된 상태라면?)
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            } else {
                model.addAttribute("nickname", "알 수 없는 사용자");
            }
        }

        return "home"; // templates/home.html 파일을 렌더링
    }

    // 2. 회원가입 화면 열기
    @GetMapping("/join")
    public String signupForm(@PathVariable String loginType, Model model) {
        model.addAttribute("loginType", loginType);
        model.addAttribute("pageName", "회원가입");
        model.addAttribute("joinRequest", new UserSignupRequest()); // HTML의 th:object="${joinRequest}"와 바인딩
        return "join"; // templates/join.html 파일을 렌더링
    }

    // 3. 회원가입 처리
    @PostMapping("/join")
    public String signup(@PathVariable String loginType, @ModelAttribute("joinRequest") UserSignupRequest userSignupRequest) {
        // @RequestBody 대신 @ModelAttribute를 사용하여 폼 데이터를 수신합니다.
        // DTO와 HTML 폼 양쪽에 이름(username), 이메일(email), 비밀번호(password),
        // 닉네임(nickname), 전화번호(phone) 필드가 정확히 매핑됩니다.
        userService.signup(userSignupRequest);
        return "redirect:/"+ loginType + "/login"; // 가입 완료 후 로그인 화면으로 이동
    }

    // 4. 로그인 화면 열기
    @GetMapping("/login")
    public String loginForm(@PathVariable String loginType, Model model) {
        model.addAttribute("loginType", loginType);
        model.addAttribute("pageName", "로그인");
        model.addAttribute("loginRequest", new UserLoginRequest()); // HTML의 th:object="${loginRequest}"와 바인딩
        return "login"; // templates/login.html 파일을 렌더링
    }

    // 5. 로그인 처리 및 JWT 발급
    @PostMapping("/login")
    public String login(@PathVariable String loginType,
                        @ModelAttribute("loginRequest") UserLoginRequest userLoginRequest,
                        HttpServletResponse response,
                        Model model) { // 💡 에러 메시지 전달을 위해 Model 추가

        // 💡 폼 데이터가 잘 들어오는지 확인용 로그 (필요 없으면 지우셔도 됩니다)
        System.out.println("전달받은 이메일: " + userLoginRequest.getEmail());
        System.out.println("전달받은 비밀번호: " + userLoginRequest.getPassword());

        try {
            String token = userService.authenticateAndGenerateToken(userLoginRequest);

            // 생성된 JWT 토큰을 브라우저의 쿠키(Cookie)에 저장하여 다음 요청 시 활용할 수 있게 합니다.
            Cookie jwtCookie = new Cookie("jwtToken", token);
            jwtCookie.setPath("/"); // 모든 경로에서 이 쿠키를 사용할 수 있도록 설정
            jwtCookie.setHttpOnly(true); // 자바스크립트에서 접근할 수 없도록 하여 XSS 공격 방어
            response.addCookie(jwtCookie); // 응답에 쿠키 추가

            return "redirect:/" + loginType + "/home"; // 로그인 성공 후 홈 화면으로 이동

        } catch (AuthenticationException e) {
            System.out.println("로그인 실패 원인: " + e.getMessage());
            model.addAttribute("loginError", "아이디 또는 비밀번호가 일치하지 않습니다.");
            model.addAttribute("loginType", loginType);
            return "login";
        }
    }

    // 6. 회원 탈퇴
    @PostMapping("/withdraw")
    public String withdraw(@PathVariable String loginType, Principal principal, HttpServletResponse response) {
        userService.deleteUser(principal.getName());

        Cookie expiredCookie = new Cookie("jwtToken", null);
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        response.addCookie(expiredCookie);

        System.out.println("탈퇴 전달받은 이메일: " + principal.getName());

        return "redirect:/" + loginType + "/login";
    }

    // 7. 회원정보 조회 화면
    @GetMapping("/info")
    public String infoForm(@PathVariable String loginType, Principal principal, Model model) {
        Users loginUser = userService.findByEmail(principal.getName());
        model.addAttribute("loginType", loginType);
        model.addAttribute("pageName", "회원정보");
        model.addAttribute("user", loginUser);
        model.addAttribute("updateRequest", new UserUpdateRequest());
        return "info";
    }

    // 7. 회원정보 수정 처리
    @PostMapping("/info")
    public String updateInfo(@PathVariable String loginType,
                             @ModelAttribute("updateRequest") UserUpdateRequest updateRequest,
                             Principal principal,
                             Model model) {
        try {
            userService.updateUser(principal.getName(), updateRequest);
            return "redirect:/" + loginType + "/info?success";
        } catch (IllegalArgumentException e) {
            Users loginUser = userService.findByEmail(principal.getName());
            model.addAttribute("loginType", loginType);
            model.addAttribute("pageName", "회원정보");
            model.addAttribute("user", loginUser);
            model.addAttribute("updateRequest", updateRequest);
            model.addAttribute("updateError", e.getMessage());
            return "info";
        }
    }
}