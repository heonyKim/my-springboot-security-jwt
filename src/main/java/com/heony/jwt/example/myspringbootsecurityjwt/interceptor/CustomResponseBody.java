package com.heony.jwt.example.myspringbootsecurityjwt.interceptor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class CustomResponseBody {

    private Enum<?> status;
    private int statusCode;
    private String message;
    private Object data;

    public CustomResponseBody() {
        this.status = HttpStatus.BAD_REQUEST;
        this.statusCode = HttpStatus.BAD_REQUEST.value();
        this.message = null;
        this.data = null;
    }

    public CustomResponseBody(Object data, int statusCode) {
        this.status = HttpStatus.valueOf(statusCode);
        this.statusCode = statusCode;
        if(statusCode>=200 && statusCode <300){
            this.message = CustomResponseBodyResult.SUCCESS.toString();
        }else{
            this.message = HttpStatus.valueOf(statusCode).getReasonPhrase();
        }
        this.data = data;
    }

    public CustomResponseBody(Object data, int statusCode, String message) {
        this.status = HttpStatus.valueOf(statusCode);
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public CustomResponseBody(HttpStatus status, int statusCode, String message, Object data) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static CustomResponseBody of(Object data, int statusCode){
        return new CustomResponseBody(data,statusCode);
    }
    public static CustomResponseBody error(Object data, int statusCode){
        return new CustomResponseBody(data,statusCode);
    }

}
