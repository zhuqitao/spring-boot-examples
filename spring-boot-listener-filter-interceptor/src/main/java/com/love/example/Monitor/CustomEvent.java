package com.love.example.Monitor;

import org.springframework.context.ApplicationEvent;

public class CustomEvent extends ApplicationEvent {
    public CustomEvent(Object source) {
        super(source);
    }

    /**
     * 事件处理
     */
//    public void doEvent() {
//        System.out.println("监听到事件："+ CustomEvent.class);
//    }
}
