package com.love.example.mapper.second;

import com.love.example.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface SecondMapper {
    User getUser(Long id);
    int addUser(User user);
}
