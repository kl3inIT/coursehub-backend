package com.coursehub.repository;

import com.coursehub.entity.ReportEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    @Query("SELECT r FROM ReportEntity r " +
            "WHERE (:type IS NULL OR r.type = :type) " +
            "AND (:severity IS NULL OR r.severity = :severity) " +
            "AND (:status IS NULL OR r.status = :status) " +
            "AND (:search IS NULL OR " +
            "LOWER(r.reason) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.reporter.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.reportedUser.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ReportEntity> findAllWithFilters(
            @Param("type") ResourceType type,
            @Param("severity") ReportSeverity severity,
            @Param("status") ReportStatus status,
            @Param("search") String search,
            Pageable pageable);


    boolean existsByReporterAndResourceIdAndType(UserEntity reporter, Long resourceId, ResourceType type);

    List<ReportEntity> findByResourceId(Long resourceId);

}
