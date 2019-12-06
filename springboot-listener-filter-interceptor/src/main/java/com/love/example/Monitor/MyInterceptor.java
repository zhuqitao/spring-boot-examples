package com.love.example.Monitor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在请求到达controller之前调用
        System.out.println("请求之前preHandle");
        // 如果返回false，则会中断执行,不会进入到postHandle和afterCompletion
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在Controller方法处理完之后DispatcherServlet进行视图的渲染之前，在这里可以对ModelAndView进行操作
        System.out.println("请求之后postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // DispatcherServlet进行视图的渲染之后的回调方法，一般用于资源清理
        System.out.println("进行视图的渲染之后回调afterCompletion");
    }
}
