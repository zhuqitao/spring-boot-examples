package com.love.example.Service;

import com.love.example.Model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> getUser(Integer id);

    List<User> getAllUser();

    Boolean addUser(User user);

    Boolean updateUser(User user);

    Boolean deleteUser(Integer id);
}
