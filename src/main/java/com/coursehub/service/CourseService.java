package com.coursehub.service;

import com.coursehub.dto.request.course.CourseCreationRequestDTO;
import com.coursehub.dto.request.course.CourseSearchRequestDTO;
import com.coursehub.dto.response.course.CourseSearchStatsResponseDTO;
import com.coursehub.dto.response.course.DashboardCourseResponseDTO;
import com.coursehub.dto.response.course.CourseDetailsResponseDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;

import com.coursehub.entity.CourseEntity;
import com.coursehub.enums.CourseLevel;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CourseService {

    CourseResponseDTO createCourse(CourseCreationRequestDTO courseRequestDTO);

    String uploadThumbnail(Long courseId, MultipartFile file);

    CourseResponseDTO findCourseById(Long courseId);

    Page<CourseResponseDTO> findAllCourse(Pageable pageable);

    List<CourseResponseDTO> findByCategoryId(Long categoryId);

    List<CourseResponseDTO> findFeaturedCourses(Pageable pageable);

    Page<CourseResponseDTO> searchCourses(String search, Long categoryId, CourseLevel level,
                                        Double minPrice, Double maxPrice, Pageable pageable);

    CourseEntity findCourseEntityById(Long courseId);

    CourseDetailsResponseDTO findCourseDetailsById(Long courseId);

    CourseEntity findCourseEntityByLessonId(Long lessonId);

    List<DashboardCourseResponseDTO> getCoursesByUserId();

    Page<CourseResponseDTO> advancedSearch(CourseSearchRequestDTO searchRequest, Pageable pageable);

    CourseSearchStatsResponseDTO getSearchStatistics();

    List<CourseResponseDTO> getCoursesRecommend();

    List<CourseResponseDTO> getCoursesByPriceASC();

    List<CourseResponseDTO> getCoursesByPriceDESC();
}    