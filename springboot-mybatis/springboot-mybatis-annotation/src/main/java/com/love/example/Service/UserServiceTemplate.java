package com.love.example.Service;

import com.love.example.mapper.MyMapper;
import com.love.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("UserService")
public class UserServiceTemplate implements UserService {
    @Autowired
    private MyMapper myMapper;

    @Override
    public User getUser(Long id) {
        return this.myMapper.getUser(id);
    }
    @Override
    public int addUser(User user) {
        return this.myMapper.addUser(user);
    }
    @Override
    public int setUser(User user) {
        return this.myMapper.setUser(user);
    }
    @Override
    public int removeUser(Long id) {
        return this.myMapper.removeUser(id);
    }
}
