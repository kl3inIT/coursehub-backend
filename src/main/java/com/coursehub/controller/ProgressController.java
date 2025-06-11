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
}
