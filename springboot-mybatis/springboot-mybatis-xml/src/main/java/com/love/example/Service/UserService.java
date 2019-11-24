package com.love.example.Service;

import com.love.example.model.User;

public interface UserService {
    User getUser(Long id);
    int addUser(User user);
    int setUser(User user);
    int removeUser(Long id);
}
