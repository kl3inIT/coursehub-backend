package com.coursehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coursehub.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmailAndIsActive(String email, Long status);
    boolean existsByEmailAndIsActive(String email, Long status);
    
    @Query("SELECT u FROM UserEntity u WHERE u.roleEntity.code IN :roles")
    Page<UserEntity> findByRoleCode(@Param("roles") List<String> roles, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.roleEntity.code IN :roles AND u.isActive = :status")
    Page<UserEntity> findByRoleCodeAndIsActive(
        @Param("roles") List<String> roles, 
        @Param("status") Long status, 
        Pageable pageable
    );

    Optional<UserEntity> findByEmail(String email);
}
