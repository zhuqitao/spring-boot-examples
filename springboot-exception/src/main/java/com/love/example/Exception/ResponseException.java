package com.love.example.Exception;

public class ResponseException extends RuntimeException {
    // 异常状态码
    private int code;
    // 异常信息
    private String message;

    public ResponseException() {
        super();
    }
    public ResponseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ResponseException(HttpStatusTypeEnum enmu, String message) {
        super(message);
        this.code = enmu.getCode();
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
