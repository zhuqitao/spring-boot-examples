package com.love.example.Exception;

public enum HttpStatusTypeEnum {
    SUCCESS(200, "成功"),
    USER_INPUT_ERROR(400, "用户输入异常"),
    SIGNATURE_NOT_MATCH(401,"请求的数字签名不匹配!"),
    SYSTEM_ERROR (500,"系统服务异常"),
    OTHER_ERROR(-1,"未知异常");

    private int code;
    private String desc;

    HttpStatusTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
