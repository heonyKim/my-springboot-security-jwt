package com.heony.jwt.example.myspringbootsecurityjwt.exception;

import com.heony.jwt.example.myspringbootsecurityjwt.interceptor.CustomResponseBody;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.logging.ErrorManager;

@Getter
public class BaseException extends RuntimeException{

    //default enum HttpStatus or ErrorMessage
    private Enum<?> status;
    private int statusCode;
    private String message;

    public BaseException() {
        super(HttpStatus.BAD_REQUEST.getReasonPhrase());

        this.status = HttpStatus.BAD_REQUEST;
        this.statusCode = HttpStatus.BAD_REQUEST.value();
        this.message = HttpStatus.BAD_REQUEST.getReasonPhrase();
    }

    public BaseException(Exception e, int statusCode) {
//        super(HttpStatus.valueOf(statusCode).getReasonPhrase());

        this.status = HttpStatus.valueOf(statusCode);
        this.statusCode = statusCode;
        this.message = e.getMessage();
    }

    public BaseException(ErrorMessage errorMessage) {
        super(errorMessage.message());

        this.status = errorMessage;
        this.statusCode = errorMessage.value();
        this.message = errorMessage.message();
    }

    public BaseException(ErrorMessage errorMessage, String message) {
        super(errorMessage.message());

        this.status = errorMessage;
        this.statusCode = errorMessage.value();
        this.message = message;
    }

    public BaseException(int statusCode, ErrorMessage errorMessage) {
        super(errorMessage.message());

        this.status = errorMessage;
        this.statusCode = statusCode;
        this.message = errorMessage.message();
    }

    public BaseException(HttpStatus status, int statusCode, String message) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
    }

}
