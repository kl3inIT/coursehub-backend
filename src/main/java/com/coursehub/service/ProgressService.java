package com.coursehub.service;

import com.coursehub.dto.request.lesson.LessonProgressDTO;
import com.coursehub.dto.request.lesson.UpdateLessonProgressRequestDTO;

public interface ProgressService {

    void updateLessonProgress(Long lessonId, UpdateLessonProgressRequestDTO updateLessonProgressRequestDTO);

    LessonProgressDTO getLessonProgress(Long lessonId);

//    List<LessonProgressDTO> getCourseProgress(Long courseId, Long userId);
}
