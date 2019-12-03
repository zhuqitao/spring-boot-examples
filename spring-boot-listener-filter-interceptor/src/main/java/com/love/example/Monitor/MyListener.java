package com.love.example.Monitor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

//@WebListener
public class MyListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        System.out.println("ServletRequestEvent 销毁");
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        System.out.println("ServletRequestEvent init");
    }
}
