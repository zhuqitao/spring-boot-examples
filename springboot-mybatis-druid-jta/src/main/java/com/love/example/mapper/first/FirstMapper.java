package com.love.example.mapper.first;

import com.love.example.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface FirstMapper {
    User getUser(Long id);
    int addUser(User user);
}