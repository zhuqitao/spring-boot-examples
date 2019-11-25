package com.love.example.Controller;

import com.love.example.Service.FirstService;
import com.love.example.Service.SecondService;
import com.love.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private FirstService firstService;

    @Autowired
    private SecondService secondService;
    @GetMapping("/getFirstUser")
    public User getFirstUser(Long id) {
        return this.firstService.getUser(id);
    }
    @GetMapping("/addFirstUser")
    public int addFirstUser(User user) {
        User user1 = new User("test_first", "1234356");
        return this.firstService.addUser(user1);
    }

    @GetMapping("/getSecondUser")
    public User getSecondUser(Long id) {
        return this.secondService.getUser(id);
    }
    @GetMapping("/addSecondUser")
    public int addSecondUser(User user) {
        User user1 = new User("test_second", "1234356");
        return this.secondService.addUser(user1);
    }
}
