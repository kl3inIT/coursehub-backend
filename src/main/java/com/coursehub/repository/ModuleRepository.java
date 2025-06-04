package com.coursehub.repository;

import com.coursehub.entity.ModuleEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity,Long> {

    @Query("SELECT MAX(m.orderNumber) FROM ModuleEntity m WHERE m.courseEntity.id = :courseId")
    Long findMaxOrderNumberByCourseId(@Param("courseId") Long courseId);

    Page<ModuleEntity> findByCourseEntityId(Long courseId, Pageable pageable);
}
