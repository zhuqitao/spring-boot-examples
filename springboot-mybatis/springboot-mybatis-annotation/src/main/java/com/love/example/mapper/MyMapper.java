package com.love.example.mapper;

import com.love.example.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
//@Mapper
public interface MyMapper {
    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results({

            @Result(property = "passWord",  column = "password"),
            @Result(property = "userName", column = "user_name")
    })
    User getUser(Long id);

    @Insert("INSERT INTO users(user_name,password) VALUES(#{userName}, #{passWord})")
    int addUser(User user);

    @Update("UPDATE users SET user_name=#{userName},password=#{passWord}, WHERE id =#{id}")
    int setUser(User user);

    @Delete("DELETE FROM users WHERE id =#{id}")
    int removeUser(Long id);

}
