package com.coursehub.service.impl;

import com.coursehub.dto.response.enrollment.EnrollmentStatusResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.EnrollmentEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.exceptions.course.CourseNotFoundException;
import com.coursehub.exceptions.course.CourseNotFreeException;
import com.coursehub.exceptions.enrollment.AlreadyEnrolledException;
import com.coursehub.exceptions.enrollment.EnrollNotFoundException;
import com.coursehub.exceptions.user.UserNotFoundException;
import com.coursehub.repository.CourseRepository;
import com.coursehub.repository.EnrollmentRepository;
import com.coursehub.repository.UserLessonRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.EnrollmentService;
import com.coursehub.service.LessonService;
import com.coursehub.service.UserService;
import com.coursehub.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonService lessonService;
    private final UserLessonRepository userLessonRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

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
                    .progress(0.0)
                    .canAccess(false)
                    .accessReason("Not enrolled in this course")
                    .build();
        }
        Boolean isCompleted = enrollment.getIsCompleted() != null && enrollment.getIsCompleted() == 1L;
        return EnrollmentStatusResponseDTO.builder()
                .enrolled(true)
                .completed(isCompleted)
                .enrollDate(enrollment.getCreatedDate())
                .progress(enrollment.getProgressPercentage())
                .canAccess(true)
                .accessReason("Enrolled in course")
                .build();
    }

    @Override
    public EnrollmentStatusResponseDTO getEnhancedEnrollmentStatus(Long courseId) {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity user = userRepository.findByEmailAndIsActive(email, 1L);
        if(user == null){
            throw new UserNotFoundException("User not found with email: " + email);
        }
        
        log.info("Checking enhanced enrollment status for user ID: {} (role: {}) and course ID: {}", 
                user.getId(), user.getRoleEntity().getCode(), courseId);
        
        // Check if user is Manager or Admin - they have automatic access
        if (UserUtils.isManager(user) || UserUtils.isAdmin(user)) {
            log.info("User {} has {} role - granting automatic access to course {}", 
                    user.getEmail(), user.getRoleEntity().getCode(), courseId);
            return EnrollmentStatusResponseDTO.builder()
                    .enrolled(true) // Virtual enrollment for managers/admins
                    .completed(false)
                    .enrollDate(null)
                    .progress(0.0)
                    .canAccess(true)
                    .accessReason("Access granted via " + user.getRoleEntity().getCode() + " role")
                    .build();
        }
        
        // For regular users (LEARNER), check actual enrollment
        EnrollmentEntity enrollment = enrollmentRepository.findByUserEntity_IdAndCourseEntity_Id(user.getId(), courseId);
        if (enrollment == null) {
            log.warn("No enrollment found for learner user ID: {} and course ID: {}", user.getId(), courseId);
            return EnrollmentStatusResponseDTO.builder()
                    .enrolled(false)
                    .completed(false)
                    .enrollDate(null)
                    .progress(0.0)
                    .canAccess(false)
                    .accessReason("Not enrolled in this course")
                    .build();
        }
        
        Boolean isCompleted = enrollment.getIsCompleted() != null && enrollment.getIsCompleted() == 1L;
        return EnrollmentStatusResponseDTO.builder()
                .enrolled(true)
                .completed(isCompleted)
                .enrollDate(enrollment.getCreatedDate())
                .progress(enrollment.getProgressPercentage())
                .canAccess(true)
                .accessReason("Enrolled in course")
                .build();
    }

    @Override
    public List<EnrollmentEntity> getEnrollmentsByUserEntityId(Long userId) {

        return enrollmentRepository.findEnrollmentEntitiesByUserEntity_Id(userId);
    }

    @Override
    public String enrollInFreeCourse(Long courseId) {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity user = userRepository.findByEmailAndIsActive(email, 1L);
        
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        EnrollmentEntity existingEnrollment = enrollmentRepository
            .findByUserEntity_IdAndCourseEntity_Id(user.getId(), courseId);
        
        if (existingEnrollment != null) {
            throw new AlreadyEnrolledException("You are already enrolled in this course");
        }

        CourseEntity course = courseRepository.findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException("Course not found"));
        
        if (course.getPrice().compareTo(BigDecimal.ZERO) != 0) {
            throw new CourseNotFreeException("This course is not free. Please use payment to enroll.");
        }
        
        // Create enrollment
        EnrollmentEntity enrollment = EnrollmentEntity.builder()
            .userEntity(user)
            .courseEntity(course)
            .isCompleted(0L)
            .progressPercentage(0.0)
            .build();
        
        enrollmentRepository.save(enrollment);
        
        log.info("User {} successfully enrolled in free course {}", user.getEmail(), course.getTitle());
        
        return "Successfully enrolled in free course";
    }


}
