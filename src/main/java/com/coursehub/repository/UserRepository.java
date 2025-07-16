package com.coursehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coursehub.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmailAndIsActive(String email, Long status);

    UserEntity findByGoogleAccountIdAndIsActive(String googleAccountId, Long status);

    boolean existsByEmailAndIsActive(String email, Long status);

    Page<UserEntity> findByRoleEntity_CodeIn(List<String> roles, Pageable pageable);

    Page<UserEntity> findByRoleEntity_CodeInAndIsActive(List<String> roles, Long status, Pageable pageable);

    Optional<UserEntity> findByEmail(String email);

    Long countByIsActive(Long isActive);
}
