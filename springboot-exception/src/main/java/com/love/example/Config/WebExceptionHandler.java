package com.love.example.Config;

import com.love.example.Exception.HttpResponse;
import com.love.example.Exception.HttpStatusTypeEnum;
import com.love.example.Exception.ResponseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class WebExceptionHandler {
    @ExceptionHandler(ResponseException.class)
    @ResponseBody
    public HttpResponse responseException(ResponseException e) {
        System.out.println("发生业务异常，原因是：" + e.getMessage());
       return HttpResponse.error(e);
    }

    // 其他异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public  HttpResponse exception(Exception e) {
        System.out.println("发生未知异常，原因是：" + e);
        return HttpResponse.error(HttpStatusTypeEnum.OTHER_ERROR.getCode(), "未知异常");
    }
}
