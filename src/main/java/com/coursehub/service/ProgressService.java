package com.coursehub.service;

import com.coursehub.dto.request.lesson.LessonProgressDTO;
import com.coursehub.dto.request.lesson.UpdateLessonProgressRequestDTO;

import java.util.List;

public interface ProgressService {

    void updateLessonProgress(Long lessonId, UpdateLessonProgressRequestDTO requestDTO);

    LessonProgressDTO getLessonProgress(Long lessonId);

    Boolean canAccessLesson(Long lessonId);

    List<Long> getCompletedLessons(Long courseId);

//    List<LessonProgressDTO> getCourseProgress(Long courseId, Long userId);
}
