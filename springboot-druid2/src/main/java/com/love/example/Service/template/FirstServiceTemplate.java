package com.love.example.Service.template;

import com.love.example.Service.FirstService;
import com.love.example.mapper.first.FirstMapper;
import com.love.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("FirstService")
public class FirstServiceTemplate implements FirstService {
    @Autowired
    private FirstMapper firstMapper;

    @Override
    public User getUser(Long id) {
        return this.firstMapper.getUser(id);
    }
    @Override
    public int addUser(User user) {
        return this.firstMapper.addUser(user);
    }
    @Override
    public int setUser(User user) {
        return this.firstMapper.setUser(user);
    }
    @Override
    public int removeUser(Long id) {
        return this.firstMapper.removeUser(id);
    }
}
