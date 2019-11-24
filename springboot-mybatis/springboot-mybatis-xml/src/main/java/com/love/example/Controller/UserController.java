package com.love.example.Controller;

import com.love.example.Service.UserService;
import com.love.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/getUser")
    public User getUser(Long id) {
        return this.userService.getUser(id);
    }
    @GetMapping("/addUser")
    public int addUser(User user) {
        User user1 = new User("test", "123456");
        return this.userService.addUser(user1);
    }

    @PostMapping("/setUser")
    public int setUser(User user) {
        return this.userService.setUser(user);
    }
    @PostMapping("/removeUser")
    public int removeUser(Long id) {
        return this.userService.removeUser(id);
    }
}
