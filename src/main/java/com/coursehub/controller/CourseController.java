package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    // Public endpoints for course browsing

    @GetMapping("/featured")
    public ResponseEntity<ResponseGeneral<List<CourseResponseDTO>>> getFeaturedCourses() {
        log.info("Fetching featured courses");
        
        List<CourseResponseDTO> featuredCourses = courseService.getFeaturedCourses();
        
        ResponseGeneral<List<CourseResponseDTO>> response = new ResponseGeneral<>();
        response.setData(featuredCourses);
        response.setMessage("Featured courses retrieved successfully");
        response.setDetail("Top 4 featured courses based on ratings and enrollments");
        
        return ResponseEntity.ok(response);
    }

    // Instructor endpoints for course management

    @PostMapping
    public ResponseEntity<ResponseGeneral<CourseResponseDTO>> createCourse(
            @Valid @RequestBody CourseRequestDTO courseRequestDTO) {
        
        log.info("Creating new course: {}",
                courseRequestDTO.getTitle());
        
        try {
            CourseResponseDTO createdCourse = courseService.createCourse(courseRequestDTO);
            
            ResponseGeneral<CourseResponseDTO> response = new ResponseGeneral<>();
            response.setData(createdCourse);
            response.setMessage("Course created successfully");
            response.setDetail("New course has been created and is ready for content");
            
            log.info("Successfully created course with ID: {}", createdCourse.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to create course: {}", e.getMessage(), e);
            
            ResponseGeneral<CourseResponseDTO> errorResponse = new ResponseGeneral<>();
            errorResponse.setMessage("Failed to create course");
            errorResponse.setDetail(e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping(value = "/{courseId}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseGeneral<String>> uploadThumbnail(
            @PathVariable Long courseId,
            @RequestParam("thumbnail") MultipartFile thumbnailFile) {
        
        log.info("Uploading thumbnail for course ID: {}", courseId);
        
        try {
            String thumbnailKey = courseService.uploadThumbnail(courseId, thumbnailFile);
            
            ResponseGeneral<String> response = new ResponseGeneral<>();
            response.setData(thumbnailKey);
            response.setMessage("Thumbnail uploaded successfully");
            response.setDetail("Course thumbnail has been updated");
            
            log.info("Successfully uploaded thumbnail for course ID: {}", courseId);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid thumbnail file for course ID: {}: {}", courseId, e.getMessage());
            
            ResponseGeneral<String> errorResponse = new ResponseGeneral<>();
            errorResponse.setMessage("Invalid file");
            errorResponse.setDetail(e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Failed to upload thumbnail for course ID: {}: {}", courseId, e.getMessage(), e);
            
            ResponseGeneral<String> errorResponse = new ResponseGeneral<>();
            errorResponse.setMessage("Failed to upload thumbnail");
            errorResponse.setDetail("An error occurred while uploading the thumbnail");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

 } 