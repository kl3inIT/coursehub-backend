package com.coursehub.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.coursehub.dto.request.course.CourseCreationRequestDTO;
import com.coursehub.dto.request.course.CourseSearchRequestDTO;
import com.coursehub.dto.request.course.CourseUpdateRequestDTO;
import com.coursehub.dto.response.course.CourseCreateUpdateResponseDTO;
import com.coursehub.dto.response.course.CourseDetailsResponseDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.dto.response.course.CourseSearchStatsResponseDTO;
import com.coursehub.dto.response.course.DashboardCourseResponseDTO;
import com.coursehub.dto.response.course.ManagerCourseResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.enums.CourseStatus;

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

    String archiveCourse(Long courseId, String currentUserGmail);

    String publishCourse(Long courseId, String currentUserGmail);

    String restoreCourse(Long courseId, String currentUserGmail);

}    