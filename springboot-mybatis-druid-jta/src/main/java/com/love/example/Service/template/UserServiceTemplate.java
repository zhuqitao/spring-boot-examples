package com.love.example.Service.template;

import com.love.example.Service.UserService;
import com.love.example.mapper.first.FirstMapper;
import com.love.example.mapper.second.SecondMapper;
import com.love.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("UserService")
@Transactional("transactionManager")
public class UserServiceTemplate implements UserService {
    @Autowired
    private FirstMapper firstMapper;

    @Autowired
    private SecondMapper secondMapper;

    @Override
    public User getFirstUser(Long id) {
        return this.firstMapper.getUser(id);
    }

    @Override
    public void addFirstUser(User user) {
        this.firstMapper.addUser(user);
    }

    @Override
    public User getSecondUser(Long id) {
        return this.secondMapper.getUser(id);
    }

    @Override
    public void addSecondUser(User user) {
        this.secondMapper.addUser(user);
    }

    @Override
    public void addAllUser(User user) {
        this.firstMapper.addUser(user);
        // 创造错误
         int a = 2/0;
        this.secondMapper.addUser(user);
    }
}
