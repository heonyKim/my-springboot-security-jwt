package com.heony.jwt.example.myspringbootsecurityjwt.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {   //요청 받을 때 단 한번만 실행됨
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX="Bearer ";
    private final TokenProvider tokenProvider;

    // 실제 필터링 로직은 doFilterInternal에 들어감
    // JWT 토큰의 인증 정보를 현재 스레드의 SecurityContext에 저장하는 역할 수행함
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //1. request header에서 토큰(Authorization key's value)을 가져옴
        String jwt = resolveToken(request);

        if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
            //2. jwt가 존재하고, 토큰이 유효하면 해당 토큰으로 Authentication을 가져온 후, SecurityContext에 저장
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        //3. 진행
        filterChain.doFilter(request,response);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
