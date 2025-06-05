package com.coursehub.repository;

import com.coursehub.entity.ModuleEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity,Long> {

    @Query("SELECT MAX(m.orderNumber) FROM ModuleEntity m WHERE m.courseEntity.id = :courseId")
    Long findMaxOrderNumberByCourseId(@Param("courseId") Long courseId);

    List<ModuleEntity> findByCourseEntityId(Long courseId);
}
