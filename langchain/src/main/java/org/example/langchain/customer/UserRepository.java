package org.example.langchain.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // 在UserRepository.java中添加
    Optional<User> findByName(String name);
    // JpaRepository已内置根据ID查询的方法findById()
}
