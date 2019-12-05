package com.love.example.UserController;

import com.love.example.Model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@Api(tags = "用户相关接口", description = "用户的增删改查")
@RequestMapping("/user")
public class UserController {
    @ApiOperation("获取用户")
    @GetMapping("/get")
    public User getUserByName(@RequestParam String name) {
        return new User(name, "123456");
    }
    @ApiOperation("添加用户")
    @PostMapping("/add")
    public Boolean addUser(@RequestBody User user) {
        return true;
    }
    @ApiOperation("修改用户")
    @PutMapping("/update")
    public Boolean updateUser(@RequestBody User user) {
        return true;
    }

    @ApiIgnore
    @ApiOperation("删除用户")
    @DeleteMapping("/delete")
    public Boolean deleteUserByName(@RequestParam String name) {
        return true;
    }
}
