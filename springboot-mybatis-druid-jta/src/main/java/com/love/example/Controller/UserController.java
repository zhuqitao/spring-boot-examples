package com.love.example.Controller;

import com.love.example.Service.UserService;
import com.love.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getfirstuser")
    public User getFirstUser(Long id) {
        return this.userService.getFirstUser(id);
    }
    @GetMapping("/addfirstuser")
    public String addFirstUser(String userName, String password) {
        User user = new User(userName, password);
        this.userService.addFirstUser(user);
        return "true";
    }

    @GetMapping("/getseconduser")
    public User getSecondUser(Long id) {
        return this.userService.getSecondUser(id);
    }
    @GetMapping("/addseconduser")
    public String addSecondUser(String userName, String password) {
        User user = new User(userName, password);
        this.userService.addSecondUser(user);
        return "true";
    }

    @GetMapping("/addalluser")
    public String addAllUser(String userName, String password) {
        User user = new User(userName, password);
        this.userService.addAllUser(user);
        return "true";
    }

}
