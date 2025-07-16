package com.coursehub.service.impl;

import com.coursehub.dto.request.lesson.LessonConfirmCreationRequestDTO;
import com.coursehub.dto.request.lesson.LessonPreparedUploadRequestDTO;
import com.coursehub.dto.request.lesson.LessonUpdateRequestDTO;
import com.coursehub.dto.response.lesson.LessonResponseDTO;
import com.coursehub.dto.response.lesson.LessonVideoUpdateResponseDTO;
import com.coursehub.entity.LessonEntity;
import com.coursehub.entity.ModuleEntity;
import com.coursehub.entity.UserLessonEntity;
import com.coursehub.exceptions.lesson.AccessDeniedException;
import com.coursehub.exceptions.lesson.LessonNotFoundException;
import com.coursehub.exceptions.module.ModuleNotFoundException;
import com.coursehub.exceptions.user.UserNotFoundException;
import com.coursehub.repository.LessonRepository;
import com.coursehub.repository.ModuleRepository;
import com.coursehub.repository.UserLessonRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.LessonService;
import com.coursehub.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
    public String getLessonPreviewUrl(Long lessonId) {
        LessonEntity lesson = getLessonEntityById(lessonId);
        if (lesson.getIsPreview() != 1L) {
            throw new AccessDeniedException("This lesson is not available for preview");
        }
        String objectKey = lesson.getS3Key();
        return s3Service.generatePermanentUrl(objectKey);
    }

    @Override
    public String getLessonVideoUrl(Long lessonId) {
        LessonEntity lesson = getLessonEntityById(lessonId);
        String objectKey = lesson.getS3Key();
        return s3Service.generatePresignedGetUrl(objectKey);
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
        if (lesson.getS3Key() != null && !lesson.getS3Key().isEmpty()) {
            try {
                s3Service.deleteObject(lesson.getS3Key());
                log.info("Successfully deleted video file: {}", lesson.getS3Key());
            } catch (Exception e) {
                // Log warning but continue with lesson deletion
                log.warn("Failed to delete video file {}: {}", lesson.getS3Key(), e.getMessage());
            }
        }

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
                .duration(entity.getDuration())
                .orderNumber(entity.getOrderNumber())
                .isPreview(entity.getIsPreview())
                .build();
    }

    @Override
    @Transactional
    public LessonResponseDTO updateLesson(Long lessonId, LessonUpdateRequestDTO requestDTO) {
        LessonEntity lesson = getLessonEntityById(lessonId);
        if (requestDTO.getTitle() != null) lesson.setTitle(requestDTO.getTitle());
        if (requestDTO.getDuration() != null) lesson.setDuration(requestDTO.getDuration());
        if (requestDTO.getOrder() != null) lesson.setOrderNumber(requestDTO.getOrder());
        if (requestDTO.getIsPreview() != null) lesson.setIsPreview(requestDTO.getIsPreview() ? 1L : 0L);
        LessonEntity updated = lessonRepository.save(lesson);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public LessonVideoUpdateResponseDTO updateLessonVideo(Long lessonId, LessonPreparedUploadRequestDTO requestDTO) {
        log.info("Updating video for lesson ID: {}", lessonId);

        LessonEntity lessonEntity = getLessonEntityById(lessonId);
        
        // Delete old video if exists
        String oldS3Key = lessonEntity.getS3Key();
        if (oldS3Key != null && !oldS3Key.isEmpty()) {
            try {
                s3Service.deleteObject(oldS3Key);
                log.info("Successfully deleted old video: {}", oldS3Key);
            } catch (Exception e) {
                // Log warning but don't fail the update
                log.warn("Failed to delete old video {}: {}", oldS3Key, e.getMessage());
            }
        }

        // Generate new S3 key for the new video
        String newS3Key = String.format("private/lessons/%d/%s", 
            lessonEntity.getModuleEntity().getId(), requestDTO.getFileName());
        
        // Update lesson with new video info
        lessonEntity.setTitle(requestDTO.getTitle());
        lessonEntity.setS3Key(newS3Key);
        
        LessonEntity updatedLesson = lessonRepository.save(lessonEntity);
        
        // Generate presigned URL for upload
        String preSignedPutUrl = s3Service.generatePresignedPutUrl(newS3Key, requestDTO.getFileType());
        
        // Return proper DTO
        return LessonVideoUpdateResponseDTO.builder()
                .lessonId(updatedLesson.getId())
                .title(updatedLesson.getTitle())
                .preSignedPutUrl(preSignedPutUrl)
                .s3Key(newS3Key)
                .fileName(requestDTO.getFileName())
                .fileType(requestDTO.getFileType())
                .build();
    }

}
