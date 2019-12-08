package com.love.example.Service.ServiceTemplate;

import com.love.example.Model.User;
import com.love.example.Repository.UserRepository;
import com.love.example.Service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service("UserService")
public class UserServiceTemplate implements UserService {
    // 注入JPA仓库对象
    @Resource
    UserRepository userRepository;

    @Override
    public Optional<User> getUser(Integer id) {

        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Boolean addUser(User user) {
        userRepository.save(user);
        return true;
    }

    @Override
    public Boolean updateUser(User user) {
        userRepository.save(user);
        return true;
    }

    @Override
    public Boolean deleteUser(Integer id) {
        userRepository.deleteById(id);
        return true;
    }
}
