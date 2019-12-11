package com.love.example.Exception;

public class HttpResponse {
    // 是否成功
    private Boolean success;
    // 响应状态码
    private int code;
    // 成功或失败的提示信息
    private String message;
    // 响应返回的数据
    private Object data;

    // 请求异常处理
    public static HttpResponse error(ResponseException e) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setSuccess(false);
        httpResponse.setCode(e.getCode());
        httpResponse.setMessage(e.getMessage());
        httpResponse.setData(null);
        return httpResponse;
    }

    // 请求异常处理
    public static HttpResponse error(int code, String message) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setSuccess(false);
        httpResponse.setCode(code);
        httpResponse.setMessage(message);
        httpResponse.setData(null);
        return httpResponse;
    }

    // 成功
    public static HttpResponse success() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setSuccess(true);
        httpResponse.setCode(200);
        httpResponse.setMessage("success");
        return httpResponse;
    }

    // 成功 有data数据
    public static HttpResponse success(Object data) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setSuccess(true);
        httpResponse.setCode(200);
        httpResponse.setMessage("success");
        httpResponse.setData(data);
        return httpResponse;
    }



    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
