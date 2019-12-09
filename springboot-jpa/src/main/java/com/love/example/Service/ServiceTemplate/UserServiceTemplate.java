package com.love.example.Service.ServiceTemplate;

import com.love.example.Model.User;
import com.love.example.Repository.UserRepository;
import com.love.example.Service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@Service("UserService")
//public class UserServiceTemplate implements UserService {
//    // 注入JPA仓库对象
//    @Resource
//    UserRepository userRepository;
//
//    @Override
//    public Optional<User> getUser(Integer id) {
//
//        return userRepository.findById(id);
//    }
//
//    @Override
//    public List<User> getAllUser() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public User getUserByName(String name) {
//        return userRepository.findByName(name);
//    }
//
//    @Override
//    public User getUserByNameAndPassword(String name, String password) {
//        return userRepository.findByNameAndPassword(name, password);
//    }
//
//    @Override
//    public Boolean addUser(User user) {
//        userRepository.save(user);
//        return true;
//    }
//
//    @Override
//    public Boolean updateUser(Integer id, String name, String password) {
//        User user = userRepository.findById(id).get();
//        user.setName(name);
//        user.setPassword(password);
//        userRepository.save(user);
//        return true;
//    }
//
//    @Override
//    public Boolean deleteUser(Integer id) {
//        userRepository.deleteById(id);
//        return true;
//    }
//}
@Service("UserService")
public class UserServiceTemplate implements UserService {
    // 注入JPA仓库对象
    @Resource
    UserRepository userRepository;

    @Override
    public List<User> getUserPage(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> userPage = userRepository.findAll(pageable);
        int totalPage = userPage.getTotalPages();
        Long totalCount = userPage.getTotalElements();
        System.out.println("totalPage" + totalPage);
        System.out.println("totalCount" + totalCount);
        List<User> userList = userPage.toList();
        return userList;
    }

    @Override
    public List<User> getUserPageBySort(Integer pageNumber, Integer pageSize) {

        Order order1 = new Order(Sort.Direction.ASC, "password");
        Order order2 = new Order(Sort.Direction.DESC, "name");

        List<Order> orderList = new ArrayList<>();
        orderList.add(order1);
        orderList.add(order2);

        Sort sort = Sort.by(orderList);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> userPage = userRepository.findAll(pageable);
        List<User> userList = userPage.toList();
        return userList;
    }

    @Override
    public User getUserByIdWithQuery(Integer id) {
        return userRepository.findUserByIdWithQuery(id);
    }
}
