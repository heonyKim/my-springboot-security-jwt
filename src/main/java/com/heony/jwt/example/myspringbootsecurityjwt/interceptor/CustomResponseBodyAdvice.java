package com.heony.jwt.example.myspringbootsecurityjwt.interceptor;

import com.heony.jwt.example.myspringbootsecurityjwt.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestControllerAdvice(basePackages = {"com.heony.jwt.example.myspringbootsecurityjwt.controller"})
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        return CustomResponseBody.of(body,servletResponse.getStatus());
    }

    @ExceptionHandler(BaseException.class)
    protected Object handleBaseException(BaseException e, HttpServletResponse response){
        return ResponseEntity.status(e.getStatusCode()).body(
                CustomResponseBody.builder()
                        .statusCode(e.getStatusCode())
                        .status(e.getStatus())
                        .message(e.getMessage())
                        .build()
        );
    }
    @ExceptionHandler(Exception.class)
    protected Object handleException(Exception e, HttpServletResponse response){
        return ResponseEntity.status(500).body(
                CustomResponseBody.builder()
                        .statusCode(500)
                        .status(HttpStatus.valueOf(500))
                        .message(e.getMessage())
                        .build()
        );
    }


}
