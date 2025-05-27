package com.coursehub.service;

import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CourseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CourseService {

    // Business Logic Methods (return DTOs)
    
    /**
     * Gets featured courses for homepage (top 4 courses)
     * @return List of featured course DTOs
     */
    List<CourseResponseDTO> getFeaturedCourses();

    /**
     * Creates a new course
     * @param courseRequestDTO The course details to create
     * @param instructorId The ID of the instructor creating the course
     * @return The created course DTO
     */
    CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO);

    /**
     * Uploads a thumbnail for a course
     * @param courseId The ID of the course
     * @param file The thumbnail file to upload
     * @return The object key of the uploaded file
     */
    String uploadThumbnail(Long courseId, MultipartFile file) throws IOException;
}    