package com.love.example.Service;

import com.love.example.model.User;

public interface UserService {
    User getFirstUser(Long id);
    void addFirstUser(User user);
    User getSecondUser(Long id);
    void addSecondUser(User user);

    void addAllUser(User user);
}
