package com.love.example.Service;

import com.love.example.Model.User;
import java.util.List;
import java.util.Optional;

//public interface UserService {
//
//    Optional<User> getUser(Integer id);
//
//    User getUserByName(String name);
//
//    User getUserByNameAndPassword(String name, String password);
//
//    List<User> getAllUser();
//
//    Boolean addUser(User user);
//
//    Boolean updateUser(Integer id, String name, String password);
//
//    Boolean deleteUser(Integer id);
//
//}

public interface UserService {

    List<User> getUserPage(Integer pageNumber, Integer pageSize);
    List<User> getUserPageBySort(Integer pageNumber, Integer pageSize);

    User getUserByIdWithQuery(Integer id);

}
