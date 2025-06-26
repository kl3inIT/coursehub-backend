package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.response.course.*;
import com.coursehub.dto.request.course.*;
import com.coursehub.enums.CourseLevel;
import com.coursehub.enums.CourseStatus;
import com.coursehub.service.CourseService;
import com.coursehub.service.EnrollmentService;
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

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.coursehub.constant.Constant.CommonConstants.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<ResponseGeneral<CourseCreateUpdateResponseDTO>> createCourse(
            @Valid @RequestBody CourseCreationRequestDTO courseRequestDTO) {

        log.info("Creating new course: {}", courseRequestDTO.getTitle());

        CourseCreateUpdateResponseDTO createdCourse = courseService.createCourse(courseRequestDTO);

        ResponseGeneral<CourseCreateUpdateResponseDTO> response = new ResponseGeneral<>();
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

    @GetMapping("/status/courses")
    public ResponseEntity<ResponseGeneral<List<ManagerCourseResponseDTO>>> findAllCourseByCourseStatus(CourseStatus status) {
        log.info("Finding all courses");
        List<ManagerCourseResponseDTO> courses = courseService.findAllCourseByStatus(status);
        ResponseGeneral<List<ManagerCourseResponseDTO>> response = new ResponseGeneral<>();
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

    @GetMapping("/status/statuses")
    public ResponseEntity<ResponseGeneral<Map<String, String>>> getCourseStatuses() {
        ResponseGeneral<Map<String, String>> response = new ResponseGeneral<>();
        response.setData(CourseStatus.getCourseStatuses());
        response.setMessage("Course statuses fetched successfully");
        response.setDetail("All course statuses");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/levels/levels")
    public ResponseEntity<ResponseGeneral<Map<String, String>>> getCourseLevels(){
        ResponseGeneral<Map<String, String>> response = new ResponseGeneral<>();
        response.setData(CourseLevel.getCourseLevels());
        response.setMessage("Course levels fetched successfully");
        response.setDetail("All course levels");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<ResponseGeneral<CourseCreateUpdateResponseDTO>> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseUpdateRequestDTO courseRequestDTO) {

        log.info("Updating course with ID: {}", courseId);

        CourseCreateUpdateResponseDTO updatedCourse = courseService.updateCourse(courseId, courseRequestDTO);

        ResponseGeneral<CourseCreateUpdateResponseDTO> response = new ResponseGeneral<>();
        response.setData(updatedCourse);
        response.setMessage(SUCCESS);
        response.setDetail("Course updated successfully");

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/courses/{id}/archive")
    public ResponseEntity<ResponseGeneral<String>> archiveCourse(@PathVariable Long id, Principal principal) {
        String msg = courseService.archiveCourse(id, principal.getName());
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setData("ARCHIVED");
        response.setMessage(SUCCESS);
        response.setDetail(msg);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ResponseGeneral<String>> publishCourse(
            @PathVariable Long id,
            Principal principal) {

        String msg = courseService.publishCourse(id, principal.getName());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setData("PUBLISHED");
        response.setMessage("SUCCESS");
        response.setDetail(msg);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ResponseGeneral<String>> restoreCourse(
            @PathVariable Long id,
            Principal principal) {

        String msg = courseService.restoreCourse(id, principal.getName());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setData("RESTORED");
        response.setMessage("SUCCESS");
        response.setDetail(msg);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<ResponseGeneral<String>> enrollInFreeCourse(@PathVariable Long courseId) {
        log.info("Enrolling user in free course with ID: {}", courseId);
        
        String result = enrollmentService.enrollInFreeCourse(courseId);
        
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setData(result);
        response.setMessage(SUCCESS);
        response.setDetail("Successfully enrolled in free course");
        
        return ResponseEntity.ok(response);
    }


}