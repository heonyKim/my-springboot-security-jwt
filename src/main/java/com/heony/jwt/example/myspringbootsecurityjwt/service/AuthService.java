package com.heony.jwt.example.myspringbootsecurityjwt.service;

import com.heony.jwt.example.myspringbootsecurityjwt.GlobalVariables;
import com.heony.jwt.example.myspringbootsecurityjwt.entity.Member;
import com.heony.jwt.example.myspringbootsecurityjwt.jwt.TokenProvider;
import com.heony.jwt.example.myspringbootsecurityjwt.model.MemberRequestDto;
import com.heony.jwt.example.myspringbootsecurityjwt.model.MemberResponseDto;
import com.heony.jwt.example.myspringbootsecurityjwt.model.TokenDto;
import com.heony.jwt.example.myspringbootsecurityjwt.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional
    public MemberResponseDto signUp(MemberRequestDto memberRequestDto){
        if(memberRepository.existsByEmail(memberRequestDto.getEmail())){
            throw new RuntimeException("이미 가입된 유저입니다.");
        }
        Member member = memberRequestDto.toMember(passwordEncoder);
        return MemberResponseDto.of(memberRepository.save(member));
    }

    @Transactional
    public TokenDto login(MemberRequestDto memberRequestDto, HttpServletResponse response){

        // 1. 로그인 ID/PW 기반으로 authenticationToken 생성 -> principal=email, credential=password
        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();

        // 2. 실검증 ( 사용자 비밀번호 체크 )이 이루어지는 부분
        /*
        * authenticationManagerBuilder.getObject().authenticate가 실행될 때, 내부에서 UserDetailService의 loadUserByUsername 메서드가 호출되는데
        * 이 때, UserDetailService를 상속받는 CustomUserDetailService 클래스 에서 재구성된 loadUserByUsername 메서드가 실행됨
        * */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        ResponseCookie responseCookie =
                ResponseCookie.from("refreshToken",tokenDto.getRefreshToken())
                        .maxAge(GlobalVariables.REFRESH_TOKEN_EXPIRED_TIME)
                        .path("/")
                        .secure(true)
                        .httpOnly(true)
//                        .sameSite("None")
                        .build();
        response.setHeader("Set-Cookie",responseCookie.toString());
        // (리프레시를 위한 보관용) 4. RefreshToken 저장
        this.saveRefreshToken(authentication.getName(),tokenDto.getRefreshToken());

        return TokenDto.builder().accessToken("Bearer "+tokenDto.getAccessToken()).build();
    }

    @Transactional
    public TokenDto reissue(TokenDto originTokenDto, HttpServletResponse response){

        // 1. Refresh Token 검증
        if(!tokenProvider.validateToken(originTokenDto.getRefreshToken())){
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. Access Token에서 MemberId 가져오기
        Authentication authentication = tokenProvider.getAuthentication(originTokenDto.getAccessToken());

        // 3. 리프레시를 위한 보관용 저장소에서  MemberId를 기반으로 Refresh Token값 가져오기
        String refreshTokenStr = Optional.ofNullable(stringRedisTemplate.opsForValue().get(authentication.getName()))
                .orElseThrow(() -> new RuntimeException("Refresh Token이 유효하지 않습니다."));

        // 4. request로 들어온 Refresh Token 이, 현재 보관된 Refresh Token과 일치하는지 검사
        log.trace("refreshToken.getValue() : " + refreshTokenStr);
        log.trace("originTokenDto.getRefreshToken() : " + originTokenDto.getRefreshToken());
        if(!refreshTokenStr.equals(originTokenDto.getRefreshToken())){
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 만료시간 뒤에 요청이 올 것이므로, 새로운 토큰을 생성해야함
        TokenDto newTokenDto = tokenProvider.generateTokenDto(authentication);
        ResponseCookie responseCookie =
                ResponseCookie.from("refreshToken",newTokenDto.getRefreshToken())
                        .maxAge(GlobalVariables.REFRESH_TOKEN_EXPIRED_TIME)
                        .path("/")
                        .secure(true)
                        .httpOnly(true)
//                        .sameSite("None")
                        .build();
        response.setHeader("Set-Cookie",responseCookie.toString());
        // 6. (리프레시를 위한 보관용) 저장소 업데이트
        this.saveRefreshToken(authentication.getName(),refreshTokenStr);

        // 토큰 새로 발급
        return TokenDto.builder().accessToken("Bearer "+newTokenDto.getAccessToken()).build();
    }

    private void saveRefreshToken(String keyName, String refreshToken){
        if(stringRedisTemplate.opsForValue().get(keyName)!=null){
            stringRedisTemplate.delete(keyName);
        }
        stringRedisTemplate.opsForValue().set(keyName,refreshToken);
        stringRedisTemplate.expire(keyName, GlobalVariables.REFRESH_TOKEN_EXPIRED_TIME, TimeUnit.MILLISECONDS);
    }

}
