package com.heony.jwt.example.myspringbootsecurityjwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Order(1)
@Slf4j
public class GlobalVariables {

    public static long ACCESS_TOKEN_EXPIRED_TIME;
    public static long REFRESH_TOKEN_EXPIRED_TIME;

    @Autowired
    public void setAccessTokenExpireTime(
            @Value("${jwt.access.timeout:30}") long timeout,
            @Value("${jwt.access.timeunit:MINUTES}") TimeUnit timeUnit) {
        log.debug("setAccessTokenExpireTime : "+timeout+timeUnit);
        ACCESS_TOKEN_EXPIRED_TIME = setExpiredTime(ACCESS_TOKEN_EXPIRED_TIME,timeout,timeUnit);

        //Exception case : 30 MINUTES
        if(ACCESS_TOKEN_EXPIRED_TIME ==0) ACCESS_TOKEN_EXPIRED_TIME = 1000L * 60 * 30;
    }

    @Autowired
    public void setRefreshTokenExpiredTime(
            @Value("${jwt.refresh.timeout:7}") long timeout,
            @Value("${jwt.refresh.timeunit:DAYS}") TimeUnit timeUnit) {
        log.debug("setRefreshTokenExpiredTime : "+timeout+timeUnit);
        REFRESH_TOKEN_EXPIRED_TIME = setExpiredTime(REFRESH_TOKEN_EXPIRED_TIME,timeout,timeUnit);

        //Exception case : 7 DAYS
        if(REFRESH_TOKEN_EXPIRED_TIME==0) REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60 * 60 * 24 * 7;
    }

    private static long setExpiredTime(long myExpiredTime,long timeout, TimeUnit timeUnit) {
        myExpiredTime = 0;
        try{
            if(timeUnit ==TimeUnit.DAYS){
                myExpiredTime = 1000L * 60 * 60 * 24 * timeout;
                return myExpiredTime;
            }
            if(timeUnit ==TimeUnit.HOURS){
                myExpiredTime = 1000L * 60 * 60 * timeout;
                return myExpiredTime;
            }
            if(timeUnit ==TimeUnit.MINUTES){
                myExpiredTime = 1000L * 60 * timeout;
                return myExpiredTime;
            }
            if(timeUnit ==TimeUnit.SECONDS){
                myExpiredTime = 1000L * timeout;
                return myExpiredTime;
            }
        }catch (Exception ignore){}
        return myExpiredTime;
    }

}
