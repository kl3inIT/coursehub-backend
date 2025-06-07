package com.coursehub.repository;

import com.coursehub.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByLessonEntity_Id(Long lessonId);
    List<CommentEntity> findByLessonEntity_IdAndIsHidden(Long lessonId, Long isHidden);
}

