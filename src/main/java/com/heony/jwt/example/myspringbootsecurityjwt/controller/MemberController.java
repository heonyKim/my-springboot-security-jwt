package com.heony.jwt.example.myspringbootsecurityjwt.controller;

import com.heony.jwt.example.myspringbootsecurityjwt.model.MemberResponseDto;
import com.heony.jwt.example.myspringbootsecurityjwt.service.MemberService;
import com.heony.jwt.example.myspringbootsecurityjwt.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberResponseDto> findMemberInfoById(){
        return ResponseEntity.ok(memberService.findMmemberInfoById(SecurityUtils.getCurrentMemberId()));
    }

    @GetMapping("/{email}")
    public ResponseEntity<MemberResponseDto> findMemberInfoByEmail(@PathVariable String email) {
        return ResponseEntity.ok(memberService.findMemberInfoByEmail(email));
    }



}
