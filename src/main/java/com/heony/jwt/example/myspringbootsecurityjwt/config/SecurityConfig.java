package com.heony.jwt.example.myspringbootsecurityjwt.config;

import com.heony.jwt.example.myspringbootsecurityjwt.jwt.JwtAccessDeniedHandler;
import com.heony.jwt.example.myspringbootsecurityjwt.jwt.JwtAuthenticationEntryPoint;
import com.heony.jwt.example.myspringbootsecurityjwt.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // h2 DB로의 테스트가 원할하도록 관련 API들 전부 무시
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        String[] ignoredPath = {
                "/h2-console/**",
                "/favicon.ico",
                "/swagger-ui.html"
        };
        return web -> web.ignoring().antMatchers(ignoredPath);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // Spring Security 는 기본적으로 SESSION 을 사용하지만, 여기서는 사용하지 않을것이라 STATELESS로 설정함
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 로그인, 회원가입 API( AuthController /auth/** )는 JWT TOKEN이 없는 상태에서도 요청이 들어와야 하기 때문에 permitAll 설정
                .and()
                .authorizeRequests()
                .antMatchers("/auth/**","/login/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }

    @Bean   //allow cors
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
