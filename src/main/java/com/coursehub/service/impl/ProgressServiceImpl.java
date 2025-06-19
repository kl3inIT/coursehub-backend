package com.coursehub.service.impl;

import com.coursehub.dto.request.lesson.LessonProgressDTO;
import com.coursehub.dto.request.lesson.UpdateLessonProgressRequestDTO;
import com.coursehub.entity.*;
import com.coursehub.exceptions.lesson.PreviousLessonNotFoundException;
import com.coursehub.exceptions.module.PreviousModuleNotFoundException;
import com.coursehub.repository.UserLessonRepository;
import com.coursehub.repository.LessonRepository;
import com.coursehub.repository.ModuleRepository;
import com.coursehub.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressServiceImpl implements ProgressService {

    private final UserLessonRepository userLessonRepository;
    private final UserService userService;
    private final LessonService lessonService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

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

    @Override
    public Boolean canAccessLesson(Long lessonId) {
        LessonEntity currentLesson = lessonService.getLessonEntityById(lessonId);
        ModuleEntity currentModule = currentLesson.getModuleEntity();
        UserEntity currentUser = userService.getUserBySecurityContext();

        // If it's the first lesson in the first module, allow access
        if (currentLesson.getOrderNumber() == 1 && currentModule.getOrderNumber() == 1) {
            return true;
        }

        // Check previous lesson in the same module
        if (currentLesson.getOrderNumber() > 1) {
            LessonEntity previousLesson = lessonRepository.findByModuleEntityIdAndOrderNumber(
                    currentModule.getId(),
                    currentLesson.getOrderNumber() - 1
            ).orElseThrow(() -> new PreviousLessonNotFoundException("Previous lesson not found for lesson ID: " + lessonId));

            Optional<UserLessonEntity> previousProgress = userLessonRepository.findByUserEntityAndLessonEntityIdAndIsCompleted(
                    currentUser,
                    previousLesson.getId(),
                    1L
            );

            if (previousProgress.isEmpty()) {
                return false;
            }
        }

        // Check previous module completion if this is the first lesson of a module
        if (currentLesson.getOrderNumber() == 1 && currentModule.getOrderNumber() > 1) {
            ModuleEntity previousModule = moduleRepository.findByCourseEntityIdAndOrderNumber(
                    currentModule.getCourseEntity().getId(),
                    currentModule.getOrderNumber() - 1
            ).orElseThrow(() -> new PreviousModuleNotFoundException("Previous module not found for module ID: " + currentModule.getId()));

            // Get all completed lessons in the previous module
            List<UserLessonEntity> completedLessons = userLessonRepository.findCompletedLessonsByUserAndModule(
                    currentUser.getId(),
                    previousModule.getId()
            );

            // Get all lessons in the previous module
            List<LessonEntity> previousModuleLessons = lessonRepository.findByModuleEntityId(previousModule.getId());

            // Check if all lessons in the previous module are completed
            if (completedLessons.size() != previousModuleLessons.size()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<Long> getCompletedLessons(Long courseId) {
        UserEntity currentUser = userService.getUserBySecurityContext();
        // Get all modules in the course
        List<ModuleEntity> modules = moduleRepository.findByCourseEntityId(courseId);

        // Get all lesson IDs from these modules
        List<Long> lessonIds = modules.stream()
                .flatMap(module -> lessonRepository.findByModuleEntityId(module.getId()).stream())
                .map(LessonEntity::getId)
                .toList();

        // Get all completed lessons for the current user
        return userLessonRepository.findByUserEntityAndLessonEntityIdInAndIsCompleted(
                        currentUser,
                        lessonIds,
                        1L
                ).stream()
                .map(ul -> ul.getLessonEntity().getId())
                .toList();
    }
}
