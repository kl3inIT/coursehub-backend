package com.coursehub.repository;

import com.coursehub.entity.CertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CertificateRepository extends JpaRepository<CertificateEntity, Long> {

    Long countByUserEntity_Id(Long userId);

    Boolean existsByUserEntityIdAndCourseEntityId(Long userId, Long courseID);

}
