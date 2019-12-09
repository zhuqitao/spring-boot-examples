package com.love.example.Repository;

import com.love.example.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


//public interface UserRepository extends JpaRepository<User, Integer> {
//    User findByName(String name);
//    User findByNameAndPassword(String name, String password);
//}

public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    Page<User> findAll(Pageable pageable);
    Page<User> findAllByName(String name, Pageable pageable);

    @Query("select u from User u where u.id = ?1")
    User findUserByIdWithQuery(Integer id);

    @Modifying
    @Query("delete from User where id = ?1")
    Boolean deleteUserByIdWithQuery(Long id);
}
