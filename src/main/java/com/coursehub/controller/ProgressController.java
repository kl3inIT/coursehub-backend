package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.lesson.LessonProgressDTO;
import com.coursehub.dto.request.lesson.UpdateLessonProgressRequestDTO;
import com.coursehub.service.ProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Slf4j
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/lessons/{lessonId}")
    public ResponseEntity<ResponseGeneral<LessonProgressDTO>> updateLessonProgress(
            @PathVariable Long lessonId,
            @Valid @RequestBody UpdateLessonProgressRequestDTO requestDTO) {
        log.info("Updating progress for lesson ID: {}", lessonId);

        progressService.updateLessonProgress(lessonId, requestDTO);
        LessonProgressDTO progress = progressService.getLessonProgress(lessonId);

        ResponseGeneral<LessonProgressDTO> response = new ResponseGeneral<>();
        response.setData(progress);
        response.setMessage("Progress updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<ResponseGeneral<LessonProgressDTO>> getLessonProgress(
            @PathVariable Long lessonId) {
        log.info("Getting progress for lesson ID: {}", lessonId);

        LessonProgressDTO progress = progressService.getLessonProgress(lessonId);

        ResponseGeneral<LessonProgressDTO> response = new ResponseGeneral<>();
        response.setData(progress);
        response.setMessage("Progress retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lessons/{lessonId}/access")
    public ResponseEntity<Boolean> checkLessonAccess(@PathVariable Long lessonId) {
        log.info("Checking access for lesson ID: {}", lessonId);
        boolean canAccess = progressService.canAccessLesson(lessonId);
        return ResponseEntity.ok(canAccess);
    }


    @GetMapping("/courses/{courseId}/completed-lessons")
    public ResponseEntity<ResponseGeneral<List<Long>>> getCompletedLessons(
            @PathVariable Long courseId) {
        log.info("Getting completed lessons for course ID: {}", courseId);

        List<Long> completedLessonIds = progressService.getCompletedLessons(courseId);

        ResponseGeneral<List<Long>> response = new ResponseGeneral<>();
        response.setData(completedLessonIds);
        response.setMessage("Completed lessons retrieved successfully");
        return ResponseEntity.ok(response);
    }


}
