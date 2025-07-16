package com.coursehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coursehub.dto.response.user.UserSummaryDTO;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmailAndIsActive(String email, UserStatus status);

    UserEntity findByGoogleAccountIdAndIsActive(String googleAccountId, UserStatus status);

    boolean existsByEmailAndIsActive(String email, UserStatus status);

    @Query("SELECT new com.coursehub.dto.response.user.UserSummaryDTO(" +
           "u.id, u.name, u.email, u.avatar, r.code, u.isActive, u.createdDate, " +
           "CAST(SIZE(u.enrollmentEntities) AS long), " +
           "CAST(SIZE(u.courseEntities) AS long)) " +
           "FROM UserEntity u JOIN u.roleEntity r WHERE r.code IN :roles")
    Page<UserSummaryDTO> findUserSummaries(@Param("roles") List<String> roles, Pageable pageable);

    @Query("SELECT new com.coursehub.dto.response.user.UserSummaryDTO(" +
           "u.id, u.name, u.email, u.avatar, r.code, u.isActive, u.createdDate, " +
           "CAST(SIZE(u.enrollmentEntities) AS long), " +
           "CAST(SIZE(u.courseEntities) AS long)) " +
           "FROM UserEntity u JOIN u.roleEntity r WHERE r.code IN :roles AND u.isActive = :status")
    Page<UserSummaryDTO> findUserSummariesWithStatus(@Param("roles") List<String> roles, @Param("status") UserStatus status, Pageable pageable);

}
