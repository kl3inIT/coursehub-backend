package com.coursehub.repository;

import com.coursehub.entity.LessonEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {

    List<LessonEntity> findByModuleEntityIdOrderByOrderNumberAsc(Long moduleId);

    @Query("SELECT MAX(l.orderNumber) FROM LessonEntity l WHERE l.moduleEntity.id = :moduleId")
    Long findMaxOrderNumberByModuleId(@Param("courseId") Long moduleId);
}
