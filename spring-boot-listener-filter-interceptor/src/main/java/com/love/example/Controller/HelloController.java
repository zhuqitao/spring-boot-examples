package com.love.example.Controller;

import com.love.example.Monitor.CustomEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class HelloController {
    @Resource private
    ApplicationContext applicationContext;

    @GetMapping("/hello")
    public String hello() {

        applicationContext.publishEvent(new CustomEvent("测试自定义事件"));
        return "hello";
    }
}
