package com.heony.jwt.example.myspringbootsecurityjwt.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//@Table
@Getter
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    private String rtKey;

    private String rtValue;

    @Builder
    public RefreshToken(String rtKey, String rtValue) {
        this.rtKey = rtKey;
        this.rtValue = rtValue;
    }

    public RefreshToken updateValue(String token){
        this.rtKey = token;
        return this;
    }

}
