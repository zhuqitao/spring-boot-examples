package com.love.example.Monitor;

import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

//@Order(1)
//@WebFilter(filterName = "MyFilter1", urlPatterns = {"/*"})
public class MyFilter1 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("MyFilter1 init");
    }

    @Override
    public void destroy() {
        System.out.println("MyFilter1 destroy");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 在请求到达servlet之前对request、response做一些预处理 比如设置请求编码
        System.out.println("MyFilter1 doFilter");
        // 传给下一个过滤器进行处理，如果该过滤器是最后一个过滤器，则直接交给servlet处理
        filterChain.doFilter(request, response);
    }
}
