package com.heony.jwt.example.myspringbootsecurityjwt.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null || authentication.getName()==null) {
            throw new RuntimeException("SecurityContext에 인증 정보가 없습니다.");
        }
        return Long.parseLong(authentication.getName());
    }
}
