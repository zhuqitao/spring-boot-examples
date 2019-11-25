package com.love.example.Service.template;

import com.love.example.Service.SecondService;
import com.love.example.mapper.second.SecondMapper;
import com.love.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("SecondService")
public class SecondServiceTemplate implements SecondService {
    @Autowired
    private SecondMapper secondMapper;

    @Override
    public User getUser(Long id) {
        return this.secondMapper.getUser(id);
    }
    @Override
    public int addUser(User user) {
        return this.secondMapper.addUser(user);
    }
    @Override
    public int setUser(User user) {
        return this.secondMapper.setUser(user);
    }
    @Override
    public int removeUser(Long id) {
        return this.secondMapper.removeUser(id);
    }
}
