package com.coursehub.service;

import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CourseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

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

    List<CourseResponseDTO> findAllCourses(Pageable pageable);

    List<CourseResponseDTO> findCourseByCategory(String category);

    List<CourseResponseDTO> findFeaturedCourses();
}    