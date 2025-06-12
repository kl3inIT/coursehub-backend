package com.coursehub.service.impl;

import com.coursehub.dto.request.lesson.LessonProgressDTO;
import com.coursehub.dto.request.lesson.UpdateLessonProgressRequestDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.LessonEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.entity.UserLessonEntity;
import com.coursehub.repository.UserLessonRepository;
import com.coursehub.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressServiceImpl implements ProgressService {

    private final UserLessonRepository userLessonRepository;
    private final UserService userService;
    private final LessonService lessonService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @Override
    @Transactional
    public void updateLessonProgress(Long lessonId, UpdateLessonProgressRequestDTO requestDTO) {
        UserEntity user = userService.getUserBySecurityContext();

        UserLessonEntity progress = userLessonRepository
                .findByUserEntityAndLessonEntityId(user, lessonId)
                .orElseGet(() -> {
                    LessonEntity lessonEntity = lessonService.getLessonEntityById(lessonId);
                    return UserLessonEntity.builder()
                            .userEntity(user)
                            .lessonEntity(lessonEntity)
                            .build();
                });

        boolean updated = false;

        Long currentTime = requestDTO.getCurrentTime();
        Long watchedDelta = requestDTO.getWatchedDelta();

        // Cập nhật currentTime nếu có thay đổi
        if (!Objects.equals(progress.getCurrentTime(), currentTime)) {
            progress.setCurrentTime(currentTime);
            updated = true;
        }

        // Cộng dồn watchedTime
        Long newWatchedTime = progress.getWatchedTime() + watchedDelta;
        if (!Objects.equals(progress.getWatchedTime(), newWatchedTime)) {
            progress.setWatchedTime(newWatchedTime);
            updated = true;
        }

        Long duration = progress.getLessonEntity().getDuration();
        boolean justCompleted = false;

        if (!Objects.equals(progress.getIsCompleted(), 1L) && newWatchedTime >= duration * 0.8) {
            log.info("User {} has completed lesson {}", user.getId(), lessonId);
            progress.setIsCompleted(1L);
            justCompleted = true; // dùng để trigger update Enrollment
            updated = true;
        }

        if (updated) {
            userLessonRepository.save(progress);

            if (justCompleted) {
                CourseEntity course = courseService.findCourseEntityByLessonId(lessonId);
                enrollmentService.updateCourseProgress(user.getId(), course.getId());
            }
        }
    }


    @Override
    public LessonProgressDTO getLessonProgress(Long lessonId) {
        UserEntity user = userService.getUserBySecurityContext();

        UserLessonEntity progress = userLessonRepository
                .findByUserEntityAndLessonEntityId(user, lessonId)
                .orElseGet(() -> {
                    LessonEntity lessonEntity = lessonService.getLessonEntityById(lessonId);
                    UserLessonEntity newProgress = UserLessonEntity.builder()
                            .userEntity(user)
                            .lessonEntity(lessonEntity)
                            .build();
                    return userLessonRepository.save(newProgress);
                });

        return LessonProgressDTO.builder()
                .lessonId(lessonId)
                .currentTime(progress.getCurrentTime())
                .watchedTime(progress.getWatchedTime())
                .isCompleted(progress.getIsCompleted())
                .build();
    }


}
