package com.coursehub.service;

import com.coursehub.dto.response.enrollment.EnrollmentStatusResponseDTO;
import com.coursehub.entity.EnrollmentEntity;
import com.coursehub.dto.response.course.CourseEnrollmentResponseDTO;
import com.coursehub.dto.response.course.CourseEnrollmentStatsResponseDTO;

import java.util.List;

public interface EnrollmentService {

    Long countByUserEntityId(Long userId);

    Long countByCourseEntityId(Long courseId);

    void updateCourseProgress(Long userId, Long courseId);

    EnrollmentStatusResponseDTO getEnrollmentStatus(Long courseId);

    EnrollmentStatusResponseDTO getEnhancedEnrollmentStatus(Long courseId);

    List<EnrollmentEntity> getEnrollmentsByUserEntityId(Long userId);

    String enrollInFreeCourse(Long courseId);

    List<CourseEnrollmentResponseDTO> getCourseEnrollments(Long courseId);

    CourseEnrollmentStatsResponseDTO getCourseEnrollmentStats(Long courseId);

    void unenrollStudent(Long courseId, Long studentId);
}