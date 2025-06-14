package com.coursehub.repository;

import com.coursehub.entity.CommentEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByLessonEntity_Id(Long lessonId);
    List<CommentEntity> findByLessonEntity_IdAndIsHidden(Long lessonId, Long isHidden);
    @Query("SELECT c FROM CommentEntity c JOIN FETCH c.userEntity WHERE c.id = :id")
    Optional<CommentEntity> findByIdWithUser(@Param("id") Long id);
}

