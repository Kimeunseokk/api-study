package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.UserLoginRequest;
import com.example.demo.domain.user.dto.UserSignupRequest;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller // HTML 화면 렌더링을 위한 컨트롤러
@RequestMapping("/{loginType}") // HTML 폼 주소 구조(/{loginType}/...)와 매핑
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtprovider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDatailsService userDatailsService;

    // ==============================================================================
    // [타임리프(Thymeleaf) HTML 연동용 메서드]
    // ==============================================================================

    // 1. 홈 화면 열기
    @GetMapping("/home")
    public String home(@PathVariable String loginType, Long userId, Model model) {
        model.addAttribute("loginType", loginType);
        model.addAttribute("pageName", "홈 화면");

        Users loginUser = userService.getLoginUserById(userId);
        if(loginUser != null) {
            model.addAttribute("nickname", loginUser.getNickname()); // 로그인 상태일 때 닉네임 전달
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
                        HttpServletResponse response) {

        // DTO의 email을 기준으로 스프링 시큐리티 인증을 시도합니다.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword())
        );

        // 인증 성공 시 JWT 토큰 생성
        User user = (User) authentication.getPrincipal();
        String token = jwtprovider.createToken(user.getUsername(), user.getAuthorities().iterator().next().getAuthority());

        // 생성된 JWT 토큰을 브라우저의 쿠키(Cookie)에 저장하여 다음 요청 시 활용할 수 있게 합니다.
        Cookie jwtCookie = new Cookie("jwtToken", token);
        jwtCookie.setPath("/"); // 모든 경로에서 이 쿠키를 사용할 수 있도록 설정
        jwtCookie.setHttpOnly(true); // 자바스크립트에서 접근할 수 없도록 하여 XSS 공격 방어
        response.addCookie(jwtCookie); // 응답에 쿠키 추가

        return "redirect:/" + loginType + "/home"; // 로그인 성공 후 홈 화면으로 이동
    }
}