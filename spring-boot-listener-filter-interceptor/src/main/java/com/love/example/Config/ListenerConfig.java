package com.love.example.Config;

import com.love.example.Monitor.MyListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerConfig {
    @Bean
    public ServletListenerRegistrationBean<MyListener> myListenerServletListenerRegistrationBean() {
        ServletListenerRegistrationBean<MyListener> bean = new ServletListenerRegistrationBean<>(new MyListener());
        return bean;
    }
}
