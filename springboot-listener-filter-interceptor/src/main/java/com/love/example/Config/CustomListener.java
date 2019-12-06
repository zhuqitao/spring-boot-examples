package com.love.example.Config;

import com.love.example.Monitor.CustomEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

//@Component
//public class CustomListener implements ApplicationListener<CustomEvent> {
//    @Override
//    public void onApplicationEvent(CustomEvent customEvent) {
//        System.out.println("监听到事件：" + CustomListener.class.getName() + customEvent.getSource());
//    }
//}

/**
 * -------------------------------------------------------------------------------------------------------------------
 */

@Component
public class CustomListener{
    @EventListener
    public void listener(CustomEvent customEvent) {
        System.out.println("监听到事件：" + CustomListener.class.getName() + customEvent.getSource());
    }
}
