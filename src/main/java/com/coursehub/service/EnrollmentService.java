package com.coursehub.service;

import com.coursehub.dto.response.enrollment.EnrollmentStatusResponseDTO;
import com.coursehub.entity.EnrollmentEntity;

import java.util.List;

public interface EnrollmentService {

    Long countByUserEntityId(Long userId);

    Long countByCourseEntityId(Long courseId);

    void updateCourseProgress(Long userId, Long courseId);

    EnrollmentStatusResponseDTO getEnrollmentStatus(Long courseId);

    EnrollmentStatusResponseDTO getEnhancedEnrollmentStatus(Long courseId);

    List<EnrollmentEntity> getEnrollmentsByUserEntityId(Long userId);

    String enrollInFreeCourse(Long courseId);
}
