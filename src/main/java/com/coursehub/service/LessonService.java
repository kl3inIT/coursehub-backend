package com.coursehub.service;


import com.coursehub.dto.request.lesson.LessonConfirmCreationRequestDTO;
import com.coursehub.dto.request.lesson.LessonPreparedUploadRequestDTO;
import com.coursehub.dto.response.lesson.LessonResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.LessonEntity;
import com.coursehub.entity.ModuleEntity;

import java.util.List;
import java.util.Map;

public interface LessonService {

    Map<String, Object> prepareUpload(Long moduleId, LessonPreparedUploadRequestDTO requestDTO);

    LessonResponseDTO confirmUpload(Long lessonId, LessonConfirmCreationRequestDTO requestDTO);

    LessonEntity getLessonEntityById(Long lessonId);

    LessonResponseDTO getLessonById(Long lessonId);

    List<LessonResponseDTO> getLessonsByModuleId(Long moduleId);

    void deleteLesson(Long lessonId);

    Long calculateTotalDurationByCourseId(Long courseId);

    Long calculateTotalDurationByModuleId(Long moduleId);

    Long countLessonsByCourseId(Long courseId);

    Long countLessonsByModuleId(Long moduleId);
}
