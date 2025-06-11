package com.coursehub.repository;

import com.coursehub.entity.CourseEntity;
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

    Long countLessonEntityByModuleEntityId(Long moduleId);

    @Query("SELECT COUNT(l) FROM LessonEntity l " +
            "WHERE l.moduleEntity.courseEntity.id = :courseId")
    Long countLessonsByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COALESCE(SUM(l.duration), 0) FROM LessonEntity l " +
            "WHERE l.moduleEntity.courseEntity.id = :courseId")
    Long sumDurationByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COALESCE(SUM(l.duration), 0) FROM LessonEntity l " +
            "WHERE l.moduleEntity.id = :moduleId")
    Long sumDurationByModuleId(@Param("moduleId") Long moduleId);


}
