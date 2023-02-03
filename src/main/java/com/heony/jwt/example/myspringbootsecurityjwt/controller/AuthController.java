package com.heony.jwt.example.myspringbootsecurityjwt.controller;

import com.heony.jwt.example.myspringbootsecurityjwt.model.MemberRequestDto;
import com.heony.jwt.example.myspringbootsecurityjwt.model.MemberResponseDto;
import com.heony.jwt.example.myspringbootsecurityjwt.model.TokenDto;
import com.heony.jwt.example.myspringbootsecurityjwt.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    AuthService authService;

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    private MemberResponseDto signUp(@RequestBody MemberRequestDto memberRequestDto){
        return authService.signUp(memberRequestDto);
    }

    @PostMapping("/login")
    private TokenDto login(@RequestBody MemberRequestDto memberRequestDto, HttpServletResponse response){
        return authService.login(memberRequestDto, response);
    }

    @PostMapping("/reissue")
    private TokenDto reissue(@RequestBody TokenDto tokenDto, HttpServletResponse response){
        return authService.reissue(tokenDto, response);
    }
}
