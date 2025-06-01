package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CategoryEntity;
import com.coursehub.entity.CourseEntity;
import com.coursehub.service.CourseService;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    @GetMapping("/featuredCourses")
    public ResponseEntity<ResponseGeneral<List<CourseResponseDTO>>> findFeaturedCourses(Pageable pageable) {

        log.info("Finding featured courses");
        List<CourseResponseDTO> featuredCourses = courseService.findFeaturedCourses(pageable);

        ResponseGeneral<List<CourseResponseDTO>> response = new ResponseGeneral<>();
        response.setData(featuredCourses);
        response.setMessage("Featured courses fetched successfully");
        response.setDetail("Finding featured courses successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allCourses")
    public ResponseEntity<ResponseGeneral<Page<CourseResponseDTO>>> findAll(Pageable pageable) {
        log.info("Finding all courses");
        Page<CourseResponseDTO> courses = courseService.findAll(pageable);
        ResponseGeneral<Page<CourseResponseDTO>> response = new ResponseGeneral<>();
        response.setData(courses);
        response.setMessage("All courses fetched successfully");
        response.setDetail("Finding all courses successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findByCate/{categoryCode}")
    public ResponseEntity<ResponseGeneral<List<CourseResponseDTO>>> findByCategoryId(@PathVariable Long categoryCode) {
        List<CourseResponseDTO> courses = courseService.findByCategoryId(categoryCode);
        ResponseGeneral<List<CourseResponseDTO>> response = new ResponseGeneral<>();
        response.setData(courses);
        response.setMessage("All courses fetched successfully");
        response.setDetail("Finding courses by category successfully");
        return ResponseEntity.ok(response);
    }

} 