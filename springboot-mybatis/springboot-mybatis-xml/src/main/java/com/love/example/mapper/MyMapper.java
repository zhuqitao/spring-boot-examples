package com.love.example.mapper;

import com.love.example.model.User;
import org.springframework.stereotype.Component;

//@Component
//@Mapper
public interface MyMapper {
    User getUser(Long id);
    int addUser(User user);
    int setUser(User user);
    int removeUser(Long id);
}
