package com.coursehub.service;

import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface CourseService {

    CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO);

    /**
     * Uploads a thumbnail for a course
     * @param courseId The ID of the course
     * @param file The thumbnail file to upload
     * @return The object key of the uploaded file
     */
    String uploadThumbnail(Long courseId, MultipartFile file);

    /**
     * Find course by ID
     * @param courseId The ID of the course to find
     * @return CourseResponseDTO containing course information
     */
    CourseResponseDTO findCourseById(Long courseId);

}    