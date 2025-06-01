package com.coursehub.service;

import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.util.List;

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

    Page<CourseResponseDTO> findAll(Pageable pageable);

    List<CourseResponseDTO> findByCategoryId(Long categoryId);

    List<CourseResponseDTO> findFeaturedCourses(Pageable pageable);

    /**
     * Search courses with filters
     * @param search Search term for title and description
     * @param categoryId Filter by category ID
     * @param level Filter by course level
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @param pageable Pagination parameters
     * @return Page of filtered courses
     */
    Page<CourseResponseDTO> searchCourses(String search, Long categoryId, String level, 
                                        Double minPrice, Double maxPrice, Pageable pageable);
}    