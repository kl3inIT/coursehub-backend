package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.lesson.LessonConfirmCreationRequestDTO;
import com.coursehub.dto.request.lesson.LessonPreparedUploadRequestDTO;
import com.coursehub.dto.request.lesson.LessonUpdateRequestDTO;
import com.coursehub.dto.response.lesson.LessonResponseDTO;
import com.coursehub.dto.response.lesson.LessonVideoUpdateResponseDTO;
import com.coursehub.service.CourseService;
import com.coursehub.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.coursehub.constant.Constant.CommonConstants.*;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Slf4j
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/{moduleId}/prepare-upload")
    public ResponseEntity<ResponseGeneral<Map<String, Object>>> lessonPrepareUpload(
            @PathVariable Long moduleId,
            @Valid @RequestBody LessonPreparedUploadRequestDTO requestDTO) {
        log.info("Preparing upload for lesson in module ID: {}", moduleId);

        Map<String, Object> uploadDetails = lessonService.prepareUpload(moduleId, requestDTO);

        ResponseGeneral<Map<String, Object>> response = new ResponseGeneral<>();
        response.setData(uploadDetails);
        response.setMessage(SUCCESS);
        response.setDetail("Lesson upload prepared successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{lessonId}/complete-upload")
    public ResponseEntity<ResponseGeneral<LessonResponseDTO>> completeUpload(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonConfirmCreationRequestDTO requestDTO) {
        log.info("Completing upload for lesson ID: {}", lessonId);

        LessonResponseDTO lesson = lessonService.confirmUpload(lessonId, requestDTO);

        ResponseGeneral<LessonResponseDTO> response = new ResponseGeneral<>();
        response.setData(lesson);
        response.setMessage(SUCCESS);
        response.setDetail("Lesson upload completed successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<ResponseGeneral<LessonResponseDTO>> getLessonById(
            @PathVariable Long lessonId) {
        log.info("Getting lesson by ID: {}", lessonId);

        LessonResponseDTO lesson = lessonService.getLessonById(lessonId);

        ResponseGeneral<LessonResponseDTO> response = new ResponseGeneral<>();
        response.setData(lesson);
        response.setMessage(SUCCESS);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<ResponseGeneral<List<LessonResponseDTO>>> getLessonsByModuleId(
            @PathVariable Long moduleId) {
        log.info("Getting lessons for module ID: {}", moduleId);

        List<LessonResponseDTO> lessons = lessonService.getLessonsByModuleId(moduleId);

        ResponseGeneral<List<LessonResponseDTO>> response = new ResponseGeneral<>();
        response.setData(lessons);
        response.setMessage(SUCCESS);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<ResponseGeneral<Void>> deleteLesson(
            @PathVariable Long lessonId) {
        log.info("Deleting lesson ID: {}", lessonId);

        lessonService.deleteLesson(lessonId);

        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage(SUCCESS);
        response.setDetail("Lesson deleted successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{lessonId}/preview-url")
    public ResponseEntity<String> getLessonPreviewUrl(@PathVariable Long lessonId) {
        String url = lessonService.getLessonPreviewUrl(lessonId);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/{lessonId}/video-url")
    public ResponseEntity<String> getLessonVideoUrl(@PathVariable Long lessonId) {
        log.info("Getting video URL for lesson ID: {}", lessonId);
        String videoUrl = lessonService.getLessonVideoUrl(lessonId);
        return ResponseEntity.ok(videoUrl);
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<ResponseGeneral<LessonResponseDTO>> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonUpdateRequestDTO requestDTO) {
        log.info("Updating lesson with ID: {}", lessonId);
        LessonResponseDTO updatedLesson = lessonService.updateLesson(lessonId, requestDTO);
        ResponseGeneral<LessonResponseDTO> response = new ResponseGeneral<>();
        response.setData(updatedLesson);
        response.setMessage(SUCCESS);
        response.setDetail("Lesson updated successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{lessonId}/video")
    public ResponseEntity<ResponseGeneral<LessonVideoUpdateResponseDTO>> updateLessonVideo(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonPreparedUploadRequestDTO requestDTO) {
        log.info("Updating video for lesson ID: {}", lessonId);

        // This will delete old video and prepare for new upload
        LessonVideoUpdateResponseDTO uploadDetails = lessonService.updateLessonVideo(lessonId, requestDTO);

        ResponseGeneral<LessonVideoUpdateResponseDTO> response = new ResponseGeneral<>();
        response.setData(uploadDetails);
        response.setMessage(SUCCESS);
        response.setDetail("Lesson video update prepared successfully");

        return ResponseEntity.ok(response);
    }
}
