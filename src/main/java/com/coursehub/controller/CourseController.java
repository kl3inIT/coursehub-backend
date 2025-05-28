package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    // Instructor endpoints for course management

    @PostMapping
    public ResponseEntity<ResponseGeneral<CourseResponseDTO>> createCourse(
            @Valid @RequestBody CourseRequestDTO courseRequestDTO) {
        
        log.info("Creating new course: {}", courseRequestDTO.getTitle());
        
        CourseResponseDTO createdCourse = courseService.createCourse(courseRequestDTO);
        
        ResponseGeneral<CourseResponseDTO> response = new ResponseGeneral<>();
        response.setData(createdCourse);
        response.setMessage("Success");
        response.setDetail("Course created successfully");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{courseId}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseGeneral<String>> uploadThumbnail(
            @PathVariable Long courseId,
            @RequestParam("thumbnail") MultipartFile thumbnailFile) {
        
        log.info("Uploading thumbnail for course ID: {}", courseId);

        String thumbnailKey = courseService.uploadThumbnail(courseId, thumbnailFile);
        
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setData(thumbnailKey);
        response.setMessage("Success");
        response.setDetail("Thumbnail uploaded successfully");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<ResponseGeneral<CourseResponseDTO>> getCourseById(
            @PathVariable Long courseId) {
        
        log.info("Getting course with ID: {}", courseId);
        
        CourseResponseDTO course = courseService.findCourseById(courseId);
        
        ResponseGeneral<CourseResponseDTO> response = new ResponseGeneral<>();
        response.setData(course);
        response.setMessage("Success");
        response.setDetail("Course retrieved successfully");
        
        return ResponseEntity.ok(response);
    }

} 