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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/{loginType}") // HTML 폼 주소(/{loginType}/...)와 맞춤
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtprovider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDatailsService userDatailsService;

    // ==============================================================================
    // [현재 사용 중: 타임리프 HTML 연동용 코드 - @Controller 방식]
    // ==============================================================================

    // 1. 홈 화면 열기
    @GetMapping
    public String home(@PathVariable String loginType, Long userId, Model model) {
        model.addAttribute("loginType", loginType);
        model.addAttribute("pageName", "홈 화면");

        Users loginUser = userService.getLoginUserById(userId);
        if(loginUser == null) {
            model.addAttribute("nickname", null);
        } else {
            model.addAttribute("nickname", loginUser.getNickname());
        }
        return "home";
    }

    // 2. 회원가입 화면 열기
    @GetMapping("/join")
    public String signupForm(@PathVariable String loginType, Model model) {
        model.addAttribute("loginType", loginType);
        model.addAttribute("pageName", "회원가입");
        model.addAttribute("joinRequest", new UserSignupRequest());
        return "signup";
    }

    // 3. 회원가입 처리
    @PostMapping("/join")
    public String signup(@PathVariable String loginType, @ModelAttribute("joinRequest") UserSignupRequest userSignupRequest) {
        userService.signup(userSignupRequest);
        return "redirect:/" + loginType;
    }

    // 4. 로그인 화면 열기
    @GetMapping("/login")
    public String loginForm(@PathVariable String loginType, Model model) {
        model.addAttribute("loginType", loginType);
        model.addAttribute("pageName", "로그인");
        model.addAttribute("loginRequest", new UserLoginRequest());
        return "login";
    }

    // 5. 로그인 처리 (JWT를 쿠키에 담아서 응답)
    @PostMapping("/login")
    public String login(@PathVariable String loginType,
                        @ModelAttribute("loginRequest") UserLoginRequest userLoginRequest,
                        HttpServletResponse response) {

        // HTML의 th:field="*{loginId}"와 맞추기 위해 getLoginId()를 사용합니다.
        // (만약 DTO에 아직 email로 되어있다면 HTML이나 DTO 둘 중 하나를 통일해주세요!)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getLoginId(), userLoginRequest.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtprovider.createToken(user.getUsername(), user.getAuthorities().iterator().next().getAuthority());

        // 브라우저가 토큰을 기억할 수 있도록 쿠키에 저장
        Cookie jwtCookie = new Cookie("jwtToken", token);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);

        return "redirect:/" + loginType;
    }

    // ==============================================================================
    // [백업: 기존 Postman 테스트 및 향후 앱 연동용 API 코드 - 주석 처리됨]
    // - 나중에 플러터나 리액트와 연결할 때 주석을 해제하고 @RestController로 변경하세요.
    // - 참고: @RequestMapping("/{loginType}") 경로 아래에 있으므로,
    //   나중에 API용으로 쓸 때는 @RequestMapping("/api/users") 등으로 분리하는 것이 좋습니다.
    // ==============================================================================
    /*
    @GetMapping("/api-home")
    public ResponseEntity<?> homeApi(Long userId){
        Users loginUser = userService.getLoginUserById(userId);
        if(loginUser == null) {
            return ResponseEntity.ok().body(
                    Map.of(
                            "login", false,
                            "loginType", "cookie-login"
                    )
            );
        }
        return ResponseEntity.ok().body(
                Map.of(
                        "login", true,
                        "loginType", "cookie-login",
                        "nickname", loginUser.getNickname()
                )
        );
    }

    @PostMapping("/signup") // 회원가입
    public ResponseEntity<?> signupApi(@RequestBody UserSignupRequest userSignupRequest) {
        userService.signup(userSignupRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login") // 로그인
    public ResponseEntity<?> loginApi(@RequestBody UserLoginRequest userLoginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String token = jwtprovider.createToken(user.getUsername(), user.getAuthorities().iterator().next().getAuthority());
        return ResponseEntity.ok().body(token);
    }
    */
}