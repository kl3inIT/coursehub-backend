package com.coursehub.service.impl;

import com.coursehub.converter.CourseConverter;
import com.coursehub.converter.EnrollmentConverter;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.dto.response.enrollment.EnrollmentResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.EnrollmentEntity;
import com.coursehub.repository.EnrollmentRepository;
import com.coursehub.service.EnrollmentService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentConverter enrollmentConverter;

    @Override
    public Long countByUserEntityId(Long userId) {
        return enrollmentRepository.countByUserEntity_Id(userId);
    }

    @Override
    public Page<EnrollmentResponseDTO> findByUserEntityId(Long userId, Pageable pageable) {
        log.info("Find Enrollment by UserEntity");
        Page<EnrollmentEntity> enrollmentEntities = enrollmentRepository.findEnrollmentsByUserId(userId, pageable);
        if (enrollmentEntities.isEmpty()) {
            throw new NullPointerException("No Enrollment found for UserEntity");
        }
        return enrollmentConverter.toResponseDTOPage(enrollmentEntities);
    }


}
