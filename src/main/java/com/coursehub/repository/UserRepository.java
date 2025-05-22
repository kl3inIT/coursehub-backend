package com.coursehub.repository;

import com.coursehub.entity.UserEntity;
import com.coursehub.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {
//    UserEntity findByUsername(String username);
}
