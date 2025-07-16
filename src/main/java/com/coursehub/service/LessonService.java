package com.coursehub.service;


import com.coursehub.dto.request.lesson.LessonConfirmCreationRequestDTO;
import com.coursehub.dto.request.lesson.LessonPreparedUploadRequestDTO;
import com.coursehub.dto.request.lesson.LessonUpdateRequestDTO;
import com.coursehub.dto.response.lesson.LessonResponseDTO;
import com.coursehub.dto.response.lesson.LessonVideoUpdateResponseDTO;
import com.coursehub.entity.LessonEntity;

import java.util.List;
import java.util.Map;

public interface LessonService {

    Map<String, Object> prepareUpload(Long moduleId, LessonPreparedUploadRequestDTO requestDTO);

    LessonResponseDTO confirmUpload(Long lessonId, LessonConfirmCreationRequestDTO requestDTO);

    LessonEntity getLessonEntityById(Long lessonId);

    LessonResponseDTO getLessonById(Long lessonId);

    List<LessonResponseDTO> getLessonsByModuleId(Long moduleId);

    void deleteLesson(Long lessonId);

    LessonVideoUpdateResponseDTO updateLessonVideo(Long lessonId, LessonPreparedUploadRequestDTO requestDTO);

    Long calculateTotalDurationByCourseId(Long courseId);

    Long calculateTotalDurationByModuleId(Long moduleId);

    Long countLessonsByCourseId(Long courseId);

    Long countLessonsByModuleId(Long moduleId);

    String getLessonPreviewUrl(Long lessonId);

    String getLessonVideoUrl(Long lessonId);

    LessonResponseDTO updateLesson(Long lessonId, LessonUpdateRequestDTO requestDTO);

}
