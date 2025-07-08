package com.coursehub.repository;

import com.coursehub.entity.EnrollmentEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {

    Long countByUserEntity_Id(Long userId);

    Long countByCourseEntity_Id(Long courseId);

    List<EnrollmentEntity> findEnrollmentEntitiesByUserEntity_Id(Long userId);

    EnrollmentEntity findByUserEntity_IdAndCourseEntity_Id(Long userEntityId, Long courseEntityId);

    @Query("SELECT COUNT(DISTINCT e.userEntity) FROM EnrollmentEntity e WHERE e.createdDate BETWEEN :startDate AND :endDate")
    Long countDistinctUserIdByCreatedAtBetween(@Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate);


}
