package com.coursehub.service;

import com.coursehub.dto.request.course.CourseCreationRequestDTO;
import com.coursehub.dto.request.course.CourseSearchRequestDTO;
import com.coursehub.dto.request.course.CourseUpdateRequestDTO;
import com.coursehub.dto.response.course.*;

import com.coursehub.entity.CourseEntity;
import com.coursehub.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CourseService {

    CourseCreateUpdateResponseDTO createCourse(CourseCreationRequestDTO courseRequestDTO);

    String uploadThumbnail(Long courseId, MultipartFile file);

    CourseResponseDTO findCourseById(Long courseId);

    List<ManagerCourseResponseDTO> findAllCourseByStatus(CourseStatus status);

    List<CourseResponseDTO> findByCategoryId(Long categoryId);

    List<CourseResponseDTO> findFeaturedCourses(Pageable pageable);

    CourseEntity findCourseEntityById(Long courseId);

    CourseDetailsResponseDTO findCourseDetailsById(Long courseId);

    CourseEntity findCourseEntityByLessonId(Long lessonId);

    List<DashboardCourseResponseDTO> getCoursesByUserId();

    Page<CourseResponseDTO> advancedSearch(CourseSearchRequestDTO searchRequest, Pageable pageable);

    CourseSearchStatsResponseDTO getSearchStatistics();

    List<CourseResponseDTO> getCoursesRecommend();

    CourseCreateUpdateResponseDTO updateCourse(Long courseId, CourseUpdateRequestDTO courseRequestDTO);

}    