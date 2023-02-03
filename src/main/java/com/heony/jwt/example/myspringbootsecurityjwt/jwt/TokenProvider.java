package com.heony.jwt.example.myspringbootsecurityjwt.jwt;

import com.google.gson.Gson;
import com.heony.jwt.example.myspringbootsecurityjwt.GlobalVariables;
import com.heony.jwt.example.myspringbootsecurityjwt.model.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE  = "Bearer";
    private final Key key;

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(Authentication authentication) {
        //권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(","));  // ROLE_USER,ROLE_ADMIN,...

        long now = System.currentTimeMillis();

        log.debug("now+GlobalVariables.ACCESS_TOKEN_EXPIRED_TIME : " + now+GlobalVariables.ACCESS_TOKEN_EXPIRED_TIME);
        //접근 토큰(AccessToken) 생성
        LocalDateTime accessTokenExpiresIn = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(now+GlobalVariables.ACCESS_TOKEN_EXPIRED_TIME), TimeZone.getDefault().toZoneId()
        );
        log.debug("accessTokenExpiresIn : " + accessTokenExpiresIn);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())   // payload "sub": "name"
                .claim(AUTHORITIES_KEY, authorities)    // payload "auth": "ROLE_USER" or "ROLE_ADMIN" or "ROLE_USER,ROLE_ADMIN,..." OR ...
                .setExpiration(Timestamp.valueOf(accessTokenExpiresIn))     // payload "exp": 1516239022 (예시)
                .signWith(key, SignatureAlgorithm.HS512)        // header "alg": "HS512"
                .compact();

        LocalDateTime refreshTokenExpiresIn = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(now+ GlobalVariables.REFRESH_TOKEN_EXPIRED_TIME), TimeZone.getDefault().toZoneId()
        );
        String refreshToken = Jwts.builder()
                .setExpiration(Timestamp.valueOf(refreshTokenExpiresIn))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(now + GlobalVariables.ACCESS_TOKEN_EXPIRED_TIME)
                .refreshToken(refreshToken)
                .build();

    }

    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화
        Claims claims = parseClaims(accessToken);

        if(claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토근입니다.");
        }

        // claims 에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)   // (s -> new SimpleGrantedAuthority(s))
                        .collect(Collectors.toList());

        // UserDetails 객체
        UserDetails userDetails = new User(claims.getSubject(),"",authorities);
        return new UsernamePasswordAuthenticationToken(userDetails,"",authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(accessToken).getBody();
    }

}
