package com.coursehub.service.impl;

import com.coursehub.dto.request.lesson.LessonConfirmCreationRequestDTO;
import com.coursehub.dto.request.lesson.LessonPreparedUploadRequestDTO;
import com.coursehub.dto.response.lesson.LessonResponseDTO;
import com.coursehub.entity.LessonEntity;
import com.coursehub.entity.ModuleEntity;
import com.coursehub.exceptions.lesson.LessonNotFoundException;
import com.coursehub.exceptions.module.ModuleNotFoundException;
import com.coursehub.repository.LessonRepository;
import com.coursehub.repository.ModuleRepository;
import com.coursehub.service.LessonService;
import com.coursehub.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final S3Service s3Service;

    @Override
    public Map<String, Object> prepareUpload(Long moduleId, LessonPreparedUploadRequestDTO requestDTO) {
        ModuleEntity moduleEntity = moduleRepository.findById(moduleId).
                orElseThrow(() -> new ModuleNotFoundException("Module not found with id: " + moduleId));
        Long maxOrderNumber = lessonRepository.findMaxOrderNumberByModuleId(moduleId);
        Long orderNumber = (maxOrderNumber == null) ? 1L : maxOrderNumber + 1;
        String s3Key = String.format("private/lessons/%d/%s", moduleId, requestDTO.getFileName());
        LessonEntity lessonEntity = LessonEntity.builder()
                .moduleEntity(moduleEntity)
                .title(requestDTO.getTitle())
                .s3Key(s3Key)
                .orderNumber(orderNumber)
                .build();
        LessonEntity savedLesson = lessonRepository.save(lessonEntity);
        String preSignedPutUrl = s3Service.generatePresignedPutUrl(s3Key, requestDTO.getFileType());
        return Map.of(
                "lessonId", savedLesson.getId(),
                "preSignedPutUrl", preSignedPutUrl,
                "s3Key", s3Key
        );
    }

    @Override
    @Transactional
    public LessonResponseDTO confirmUpload(Long lessonId, LessonConfirmCreationRequestDTO requestDTO) {

        LessonEntity lessonEntity = getLessonEntityById(lessonId);

        lessonEntity.setDuration(requestDTO.getDuration());

        LessonEntity updatedLesson = lessonRepository.save(lessonEntity);

        return mapToResponseDTO(updatedLesson);
    }

    @Override
    public LessonResponseDTO getLessonById(Long lessonId) {
        LessonEntity lessonEntity = getLessonEntityById(lessonId);
        return mapToResponseDTO(lessonEntity);
    }

    @Override
    public LessonEntity getLessonEntityById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with id: " + lessonId));
    }

    @Override
    public List<LessonResponseDTO> getLessonsByModuleId(Long moduleId) {
        List<LessonEntity> lessons = lessonRepository.findByModuleEntityIdOrderByOrderNumberAsc(moduleId);
        return lessons.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId) {
        LessonEntity lesson = getLessonEntityById(lessonId);

        // Xóa file từ S3
        s3Service.deleteObject(lesson.getS3Key());

        // Xóa lesson từ database
        lessonRepository.delete(lesson);
    }

    @Override
    public Long countLessonsByModuleId(Long moduleId) {
        if (moduleId == null) {
            return 0L;
        }
        return lessonRepository.countLessonEntityByModuleEntityId(moduleId);
    }

    @Override
    public Long countLessonsByCourseId(Long courseId) {
        if (courseId == null) {
            return 0L;
        }
        return lessonRepository.countLessonsByCourseId(courseId);
    }

    @Override
    public Long calculateTotalDurationByCourseId(Long courseId) {
        if (courseId == null) {
            return 0L;
        }
        return lessonRepository.sumDurationByCourseId(courseId);
    }

    @Override
    public Long calculateTotalDurationByModuleId(Long moduleId) {
        if (moduleId == null) {
            return 0L;
        }
        return lessonRepository.sumDurationByModuleId(moduleId);
    }

    private LessonResponseDTO mapToResponseDTO(LessonEntity entity) {
        return LessonResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .videoUrl(s3Service.generatePresignedGetUrl(entity.getS3Key()))
                .duration(entity.getDuration())
                .orderNumber(entity.getOrderNumber())
                .isPreview(entity.getIsPreview())
                .build();
    }
}
