package com.heony.jwt.example.myspringbootsecurityjwt.model;

import com.heony.jwt.example.myspringbootsecurityjwt.entity.Authority;
import com.heony.jwt.example.myspringbootsecurityjwt.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

    private String email;
    private String password;

    public Member toMember(PasswordEncoder passwordEncoder){
        return Member.builder()
                .email(this.email)
                .password(passwordEncoder.encode(this.password))
                .authority(Authority.ROLE_USER)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication(){
        return new UsernamePasswordAuthenticationToken(email,password);
    }
}
