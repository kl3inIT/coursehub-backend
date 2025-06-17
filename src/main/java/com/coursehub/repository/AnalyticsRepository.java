package com.coursehub.repository;

import com.coursehub.dto.response.analytics.CategoryAnalyticsDetailResponseDTO;
import com.coursehub.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AnalyticsRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("SELECT new com.coursehub.dto.response.analytics.CategoryAnalyticsDetailResponseDTO(\n" +
            "    c.id, \n" +
            "    c.name, \n" +
            "    c.description, \n" +
            "    COUNT(DISTINCT CASE WHEN (:startDate IS NULL OR co.createdDate >= :startDate) AND (:endDate IS NULL OR co.createdDate < :endDate) THEN co.id ELSE NULL END), \n" +
            "    COUNT(DISTINCT CASE WHEN (:startDate IS NULL OR p.modifiedDate >= :startDate) AND (:endDate IS NULL OR p.modifiedDate < :endDate) THEN p.userEntity.id ELSE NULL END), \n" +
            "    COALESCE(SUM(CASE WHEN p.status = 'COMPLETED' AND (:startDate IS NULL OR p.modifiedDate >= :startDate) AND (:endDate IS NULL OR p.modifiedDate < :endDate) THEN p.amount ELSE 0.0 END), 0.0), \n" +
            "    0.0, \n" +
            "    c.createdDate, \n" +
            "    c.modifiedDate\n" +
            ") \n" +
            "FROM CategoryEntity c \n" +
            "LEFT JOIN c.courseEntities co \n" +
            "LEFT JOIN co.paymentEntities p \n" +
            "GROUP BY c.id, c.name, c.description, c.createdDate, c.modifiedDate")
    Page<CategoryAnalyticsDetailResponseDTO> getCategoryAnalyticsDetails(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(CASE WHEN p.status = 'COMPLETED' THEN p.amount ELSE 0.0 END), 0.0) " +
            "FROM PaymentEntity p " +
            "WHERE (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.modifiedDate < :endDate)")
    Double getTotalRevenue(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}