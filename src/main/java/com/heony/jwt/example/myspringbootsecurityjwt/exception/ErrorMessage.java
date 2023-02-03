package com.heony.jwt.example.myspringbootsecurityjwt.exception;

public enum ErrorMessage {
    SIGNUP_ID_BAD_REQUEST(400, "아이디를 입력하세요"),
    SQL_INSERT_FAIL(500, "INSERT 중 에러가 발생하였습니다."),
    SQL_SELECT_FAIL(500, "SELECT 중 에러가 발생하였습니다."),
    SQL_UPDATE_FAIL(500, "UPDATE 중 에러가 발생하였습니다."),
    SQL_DELETE_FAIL(500, "DELETE 중 에러가 발생하였습니다.");

    private final int statusCode;
    private final String message;

    private ErrorMessage(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int value() {return this.statusCode;}
    public String message() {return this.message;}
}
