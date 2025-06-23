package com.coursehub.repository;

import java.util.List;
import java.util.Optional;

import com.coursehub.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coursehub.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmailAndIsActive(String email, UserStatus status);

    UserEntity findByGoogleAccountIdAndIsActive(String googleAccountId, UserStatus status);

    boolean existsByEmailAndIsActive(String email, UserStatus status);

    Page<UserEntity> findByRoleEntity_CodeIn(List<String> roles, Pageable pageable);

    Page<UserEntity> findByRoleEntity_CodeInAndIsActive(List<String> roles, UserStatus status, Pageable pageable);

    Optional<UserEntity> findByEmail(String email);

}
