package com.coursehub.repository;

import com.coursehub.entity.UserEntity;
import com.coursehub.entity.UserLessonEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonRepository extends JpaRepository<UserLessonEntity, Long> {

    Optional<UserLessonEntity> findByUserEntityAndLessonEntityId(UserEntity user, Long lessonId);

    Optional<UserLessonEntity> findByUserEntityAndLessonEntityIdAndIsCompleted(UserEntity user, Long lessonId, Long isCompleted);

    List<UserLessonEntity> findByUserEntityAndLessonEntityIdInAndIsCompleted(UserEntity user, List<Long> lessonIds, Long isCompleted);

    @Query("""
            SELECT COUNT(ul)
            FROM UserLessonEntity ul
            WHERE ul.userEntity.id = :userId
              AND ul.lessonEntity.moduleEntity.courseEntity.id = :courseId
              AND ul.isCompleted = 1
            """)
    Long countCompletedLessonsByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("""
            SELECT ul
            FROM UserLessonEntity ul
            WHERE ul.userEntity.id = :userId
              AND ul.lessonEntity.moduleEntity.id = :moduleId
              AND ul.isCompleted = 1
            """)
    List<UserLessonEntity> findCompletedLessonsByUserAndModule(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

    @Query("SELECT SUM(ul.watchedTime) FROM UserLessonEntity ul " +
           "JOIN ul.lessonEntity l " +
           "JOIN l.moduleEntity m " +
           "WHERE ul.userEntity.id = :userId AND m.courseEntity.id = :courseId")
    Long getTotalWatchedTimeByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
