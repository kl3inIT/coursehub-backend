package com.coursehub.repository;

import com.coursehub.entity.EnrollmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT MONTH(e.createdDate) as month, COUNT(DISTINCT e.userEntity) as total FROM EnrollmentEntity e WHERE YEAR(e.createdDate) = :year GROUP BY MONTH(e.createdDate)")
    List<Object[]> countStudentEnrollmentsByMonth(@Param("year") int year);

    @Query("SELECT e.courseEntity.id, e.courseEntity.title, COUNT(DISTINCT e.userEntity.id) " +
           "FROM EnrollmentEntity e " +
           "WHERE YEAR(e.createdDate) = :year " +
           "GROUP BY e.courseEntity.id, e.courseEntity.title " +
           "ORDER BY COUNT(DISTINCT e.userEntity.id) DESC")
    List<Object[]> findTopCoursesByEnrollments(@Param("year") int year);

    @Query("SELECT e.courseEntity.id, e.courseEntity.title, COUNT(DISTINCT e.userEntity.id) " +
           "FROM EnrollmentEntity e " +
           "WHERE e.createdDate BETWEEN :startDate AND :endDate " +
           "GROUP BY e.courseEntity.id, e.courseEntity.title " +
           "ORDER BY COUNT(DISTINCT e.userEntity.id) DESC")
    List<Object[]> findTopCoursesByEnrollmentsAndCreatedAtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<EnrollmentEntity> findEnrollmentEntitiesByCourseEntity_Id(Long courseEntityId);
}
