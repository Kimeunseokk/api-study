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

        // Principal 객체에는 JwtFilter에서 세팅해 둔 유저의 정보(보통 username이나 email)가 들어있습니다.
        if (principal != null) {
            String username = principal.getName();

            // TODO: 나중에는 username(또는 이메일)으로 DB에서 유저를 찾아 닉네임을 띄워주시면 됩니다.
            // Users loginUser = userService.findByUsername(username);
            // model.addAttribute("nickname", loginUser.getNickname());

            // 지금은 렌더링이 잘 되는지 확인하기 위해 임시로 아이디를 출력해 봅니다.
            model.addAttribute("nickname", username + "님");
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

        } catch (AuthenticationException e) {
            // 🚨 인증 실패 (아이디/비밀번호 틀림 등) 시 403 에러 페이지로 튕기는 것을 막아줍니다!
            System.out.println("로그인 실패 원인: " + e.getMessage());

            // HTML 화면에 보여줄 에러 메시지를 담아서 다시 로그인 화면을 보여줍니다.
            model.addAttribute("loginError", "아이디 또는 비밀번호가 일치하지 않습니다.");
            model.addAttribute("loginType", loginType);

            return "login";
        }
    }
}