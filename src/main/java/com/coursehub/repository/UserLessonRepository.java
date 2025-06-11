package com.coursehub.repository;

import com.coursehub.entity.UserEntity;
import com.coursehub.entity.UserLessonEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLessonRepository extends JpaRepository<UserLessonEntity, Long> {

    Optional<UserLessonEntity> findByUserEntityAndLessonEntityId(UserEntity user, Long lessonId);

    @Query("""
                SELECT COUNT(ul)
                FROM UserLessonEntity ul
                WHERE ul.userEntity.id = :userId
                  AND ul.lessonEntity.moduleEntity.courseEntity.id = :courseId
                  AND ul.isCompleted = 1
            """)
    Long countCompletedLessonsByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
