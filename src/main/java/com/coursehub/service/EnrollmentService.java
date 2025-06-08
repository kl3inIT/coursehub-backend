package com.coursehub.service;

import com.coursehub.dto.response.enrollment.EnrollmentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentService {

    Long countByUserEntityId(Long userId);

    Page<EnrollmentResponseDTO> findByUserEntityId(Long userId, Pageable pageable);

    Long countByCourseEntityId(Long courseId);
}
