package com.heony.jwt.example.myspringbootsecurityjwt.controller;

import com.heony.jwt.example.myspringbootsecurityjwt.model.MemberRequestDto;
import com.heony.jwt.example.myspringbootsecurityjwt.model.MemberResponseDto;
import com.heony.jwt.example.myspringbootsecurityjwt.model.TokenDto;
import com.heony.jwt.example.myspringbootsecurityjwt.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private ResponseEntity<MemberResponseDto> signUp(@RequestBody MemberRequestDto memberRequestDto){
        return ResponseEntity.ok(authService.signUp(memberRequestDto));
    }

    @PostMapping("/login")
    private ResponseEntity<TokenDto> login(@RequestBody MemberRequestDto memberRequestDto, HttpServletResponse response){
        return ResponseEntity.ok(authService.login(memberRequestDto, response));
    }

    @PostMapping("/reissue")
    private ResponseEntity<TokenDto> reissue(@RequestBody TokenDto tokenDto, HttpServletResponse response){
        return ResponseEntity.ok(authService.reissue(tokenDto, response));
    }
}
