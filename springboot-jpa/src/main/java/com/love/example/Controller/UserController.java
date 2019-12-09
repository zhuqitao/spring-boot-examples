package com.love.example.Controller;

import com.love.example.Model.User;
import com.love.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

//@RestController
//@RequestMapping("/user")
//public class UserController {
//    @Autowired
//    UserService userService;
//    @GetMapping("/add")
//    public String addUser(String name, String password) {
//        User user = new User(name, password);
//        userService.addUser(user);
//        return "1";
//    }
//    @GetMapping("/get")
//    public User getUser(Integer id) {
//        Optional<User> userOptional = userService.getUser(id);
//        User user = userOptional.orElse(null);
//        return user;
//    }
//    @GetMapping("/getUserByName")
//    public User getUserByName(String name) {
//        return userService.getUserByName(name);
//    }
//    @GetMapping("/getUserByNameAndPassword")
//    public User getUserByName(String name, String password) {
//        return userService.getUserByNameAndPassword(name, password);
//    }
//    @GetMapping("/getAll")
//    public List<User> getAllUser() {
//        return userService.getAllUser();
//    }
//    @GetMapping("/update")
//    public String updateUser(Integer id, String name, String password) {
//        userService.updateUser(id, name, password);
//        return "1";
//    }
//    @GetMapping("/delete")
//    public String deleteUser(Integer id) {
//        userService.deleteUser(id);
//        return "1";
//    }
//}

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/getUserPage")
    public List<User> getUserPage(Integer pageNumber, Integer pageSize) {
        return userService.getUserPage(pageNumber,pageSize);
    }
    @GetMapping("/getUserPageBySort")
    public List<User> getUserPageBySort(Integer pageNumber, Integer pageSize){
        return userService.getUserPageBySort(pageNumber, pageSize);
    }

    @GetMapping("/getUserByIdWithQuery")
    public User getUserByIdWithQuery(Integer id) {
        return userService.getUserByIdWithQuery(id);
    }
}
