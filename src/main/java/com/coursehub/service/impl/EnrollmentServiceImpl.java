package com.coursehub.service.impl;

import com.coursehub.dto.response.enrollment.EnrollmentStatusResponseDTO;
import com.coursehub.entity.EnrollmentEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.exceptions.enrollment.EnrollNotFoundException;
import com.coursehub.exceptions.user.UserNotFoundException;
import com.coursehub.repository.EnrollmentRepository;
import com.coursehub.repository.UserLessonRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.EnrollmentService;
import com.coursehub.service.LessonService;
import com.coursehub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonService lessonService;
    private final UserLessonRepository userLessonRepository;
    private final UserRepository userRepository;

    @Override
    public Long countByUserEntityId(Long userId) {
        return enrollmentRepository.countByUserEntity_Id(userId);
    }

    @Override
    public Long countByCourseEntityId(Long courseId) {

        return enrollmentRepository.countByCourseEntity_Id(courseId);
    }

    @Override
    public void updateCourseProgress(Long userId, Long courseId) {
        log.info("Updating course progress for user ID: {} and course ID: {}", userId, courseId);

        EnrollmentEntity enrollment = enrollmentRepository.findByUserEntity_IdAndCourseEntity_Id(userId, courseId);

        if (enrollment == null) {
            throw new EnrollNotFoundException("Enrollment not found for user ID: " + userId + " and course ID: " + courseId);
        }

        Long totalLessons = lessonService.countLessonsByCourseId(courseId);

        if (totalLessons == null) {
            throw new NullPointerException("Total lessons can not be null for course ID: " + courseId);
        }

        Long completedLessons = userLessonRepository.countCompletedLessonsByUserAndCourse(userId, courseId);

        Double percentage = ((double) completedLessons / totalLessons) * 100;
        enrollment.setProgressPercentage(percentage);

        // Đánh dấu hoàn thành nếu đã học hết
        enrollment.setIsCompleted(completedLessons.equals(totalLessons) ? 1L : 0L);

        enrollmentRepository.save(enrollment);
    }

    @Override
    public EnrollmentStatusResponseDTO getEnrollmentStatus(Long courseId) {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity user = userRepository.findByEmailAndIsActive(email, 1L);
        if(user == null){
            throw new UserNotFoundException("User not found with email: " + email);
        }
        log.info("Checking enrollment status for user ID: {} and course ID: {}", user.getId(), courseId);
        EnrollmentEntity enrollment = enrollmentRepository.findByUserEntity_IdAndCourseEntity_Id(user.getId(), courseId);
        if (enrollment == null) {
            log.warn("No enrollment found for user ID: {} and course ID: {}", user.getId(), courseId);
            return EnrollmentStatusResponseDTO.builder()
                    .enrolled(false)
                    .completed(false)
                    .enrollDate(null)
                    .build();
        }
        Boolean isCompleted = enrollment.getIsCompleted() != null && enrollment.getIsCompleted() == 1L;
        return EnrollmentStatusResponseDTO.builder()
                .enrolled(true)
                .completed(isCompleted)
                .enrollDate(enrollment.getCreatedDate())
                .progress(enrollment.getProgressPercentage())
                .build();
    }

    @Override
    public List<EnrollmentEntity> getEnrollmentsByUserEntityId(Long userId) {

        return enrollmentRepository.findEnrollmentEntitiesByUserEntity_Id(userId);
    }


}
