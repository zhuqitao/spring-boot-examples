package com.love.example.Controller;

import com.love.example.Model.User;
import com.love.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/add")
    public String addUser(String name, String password) {
        User user = new User(name, password);
        userService.addUser(user);
        return "1";
    }
    @GetMapping("/get")
    public User getUser(Integer id) {
        Optional<User> userOptional = userService.getUser(id);
        User user = userOptional.orElse(null);
        return user;
    }
    @GetMapping("/getAll")
    public List<User> getAllUser() {
        return userService.getAllUser();
    }
    @GetMapping("/update")
    public String updateUser(User user) {
        userService.updateUser(user);
        return "1";
    }
    @GetMapping("/delete")
    public String deleteUser(Integer id) {
        userService.deleteUser(id);
        return "1";
    }
}
