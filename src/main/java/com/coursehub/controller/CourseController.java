package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.course.CourseCreationRequestDTO;
import com.coursehub.dto.request.course.CourseSearchRequestDTO;
import com.coursehub.dto.request.course.CourseUpdateStatusAndLevelRequestDTO;
import com.coursehub.dto.response.course.CourseDetailsResponseDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.dto.response.course.CourseSearchStatsResponseDTO;
import com.coursehub.dto.response.course.DashboardCourseResponseDTO;
import com.coursehub.enums.CourseLevel;
import com.coursehub.service.CourseService;
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

import static com.coursehub.constant.Constant.CommonConstants.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ResponseGeneral<CourseResponseDTO>> createCourse(
            @Valid @RequestBody CourseCreationRequestDTO courseRequestDTO) {

        log.info("Creating new course: {}", courseRequestDTO.getTitle());

        CourseResponseDTO createdCourse = courseService.createCourse(courseRequestDTO);

        ResponseGeneral<CourseResponseDTO> response = new ResponseGeneral<>();
        response.setData(createdCourse);
        response.setMessage(SUCCESS);
        response.setDetail("Course created successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/{courseId}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseGeneral<String>> uploadThumbnail(
            @PathVariable Long courseId,
            @RequestParam("thumbnail") MultipartFile thumbnailFile) {

        log.info("Uploading thumbnail for course ID: {}", courseId);

        String thumbnailKey = courseService.uploadThumbnail(courseId, thumbnailFile);

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setData(thumbnailKey);
        response.setMessage(SUCCESS);
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
        response.setMessage(SUCCESS);
        response.setDetail("Course retrieved successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/featured")
    public ResponseEntity<ResponseGeneral<List<CourseResponseDTO>>> findFeaturedCourses(Pageable pageable) {

        log.info("Finding featured courses");
        List<CourseResponseDTO> featuredCourses = courseService.findFeaturedCourses(pageable);
        ResponseGeneral<List<CourseResponseDTO>> response = new ResponseGeneral<>();
        response.setData(featuredCourses);
        response.setMessage("Featured courses fetched successfully");
        response.setDetail("Finding featured courses successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ResponseGeneral<Page<CourseResponseDTO>>> findAllCourse(Pageable pageable) {
        log.info("Finding all courses");
        Page<CourseResponseDTO> courses = courseService.findAllCourse(pageable);
        ResponseGeneral<Page<CourseResponseDTO>> response = new ResponseGeneral<>();
        response.setData(courses);
        response.setMessage("All courses fetched successfully");
        response.setDetail("Finding all courses successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories/{categoryCode}")
    public ResponseEntity<ResponseGeneral<List<CourseResponseDTO>>> findByCategoryId(@PathVariable Long categoryCode) {
        List<CourseResponseDTO> courses = courseService.findByCategoryId(categoryCode);
        ResponseGeneral<List<CourseResponseDTO>> response = new ResponseGeneral<>();
        response.setData(courses);
        response.setMessage("All courses fetched successfully");
        response.setDetail("Finding courses by category successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseGeneral<Page<CourseResponseDTO>>> searchCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable) {

        log.info("Searching courses with filters - search: {}, category: {}, level: {}, minPrice: {}, maxPrice: {}",
                search, category, level, minPrice, maxPrice);

        // Create search request DTO
        CourseSearchRequestDTO searchRequest = CourseSearchRequestDTO.builder()
                .searchTerm(search)
                .categoryId(category)
                .level(level != null ? level.name() : null)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .sortBy(CourseSearchRequestDTO.DEFAULT_SORT_BY)
                .sortDirection(CourseSearchRequestDTO.DEFAULT_SORT_DIRECTION)
                .build();

        // Validate price range
        searchRequest.validatePriceRange();

        Page<CourseResponseDTO> courses = courseService.advancedSearch(searchRequest, pageable);

        ResponseGeneral<Page<CourseResponseDTO>> response = new ResponseGeneral<>();
        response.setData(courses);
        response.setMessage(SUCCESS);
        response.setDetail("Search completed successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}/details")
    public ResponseEntity<ResponseGeneral<CourseDetailsResponseDTO>> getCourseDetails(
            @PathVariable Long courseId) {

        log.info("Getting course details for ID: {}", courseId);

        CourseDetailsResponseDTO courseDetails = courseService.findCourseDetailsById(courseId);

        ResponseGeneral<CourseDetailsResponseDTO> response = new ResponseGeneral<>();
        response.setData(courseDetails);
        response.setMessage(SUCCESS);
        response.setDetail("Course details retrieved successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ResponseGeneral<List<DashboardCourseResponseDTO>>> getDashboardCourses() {
        log.info("Getting dashboard courses for current user");

        List<DashboardCourseResponseDTO> dashboardCourses = courseService.getCoursesByUserId();

        ResponseGeneral<List<DashboardCourseResponseDTO>> response = new ResponseGeneral<>();
        response.setData(dashboardCourses);
        response.setMessage(SUCCESS);
        response.setDetail("Dashboard courses retrieved successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/advanced-search")
    public ResponseEntity<ResponseGeneral<Page<CourseResponseDTO>>> advancedSearch(
            @Valid CourseSearchRequestDTO searchRequest,
            Pageable pageable) {

        log.info("Advanced search with filters - {}", searchRequest);

        Page<CourseResponseDTO> courses = courseService.advancedSearch(searchRequest, pageable);

        ResponseGeneral<Page<CourseResponseDTO>> response = new ResponseGeneral<>();
        response.setData(courses);
        response.setMessage(SUCCESS);
        response.setDetail("Advanced search completed successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/stats")
    public ResponseEntity<ResponseGeneral<CourseSearchStatsResponseDTO>> getSearchStats() {
        log.info("Getting search statistics");

        CourseSearchStatsResponseDTO stats = courseService.getSearchStatistics();

        ResponseGeneral<CourseSearchStatsResponseDTO> response = new ResponseGeneral<>();
        response.setData(stats);
        response.setMessage(SUCCESS);
        response.setDetail("Search statistics retrieved successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommended")
    public ResponseEntity<ResponseGeneral<List<CourseResponseDTO>>> getCourseRecommendations() {
        log.info("Getting course recommendations");
        List<CourseResponseDTO> dtos = courseService.getCoursesRecommend();
        ResponseGeneral<List<CourseResponseDTO>> response = new ResponseGeneral<>();
        response.setData(dtos);
        response.setMessage(SUCCESS);
        response.setDetail("Course recommendations retrieved successfully");
        return ResponseEntity.ok(response);
    }


}