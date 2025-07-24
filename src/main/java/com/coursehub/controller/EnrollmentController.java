package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.response.enrollment.EnrollmentStatusResponseDTO;
import com.coursehub.service.EnrollmentService;
import com.coursehub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Slf4j
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @GetMapping("/count")
    public ResponseEntity<ResponseGeneral<Long>> countByUserId() {
        Long userId = userService.getMyInfo().getId();
        Long totalCourses = enrollmentService.countByUserEntityId(userId);
        ResponseGeneral<Long> response = new ResponseGeneral<>();
        response.setData(totalCourses);
        response.setMessage("Successfully count total enrolled courses");
        response.setDetail("Total enrolled courses: " + totalCourses);
        return ResponseEntity.ok(response);
    }


//    @GetMapping("/enrolled")
//    public ResponseEntity<ResponseGeneral<Page<EnrollmentResponseDTO>>> getByUserEntityId(Pageable pageable) {
//        Long userId = userService.getMyInfo().getId();
//        log.info("Get enrolled courses by user id: " + userId);
//        Page<EnrollmentResponseDTO> enrollmentResponseDTOS = enrollmentService.findByUserEntityId(userId, pageable);
//        ResponseGeneral<Page<EnrollmentResponseDTO>> response = new ResponseGeneral<>();
//        response.setData(enrollmentResponseDTOS);
//        response.setMessage("Successfully count total enrolled courses");
//        response.setDetail("Total enrolled courses: " + enrollmentResponseDTOS.getContent().size());
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/status/{courseId}")
    public ResponseEntity<ResponseGeneral<EnrollmentStatusResponseDTO>> getEnrollmentStatus(@PathVariable Long courseId) {
        log.info("Get enrollment status by course id: " + courseId);
        EnrollmentStatusResponseDTO enrollmentStatus = enrollmentService.getEnrollmentStatus(courseId);
        ResponseGeneral<EnrollmentStatusResponseDTO> response = new ResponseGeneral<>();
        response.setData(enrollmentStatus);
        response.setMessage("Successfully retrieved enrollment status");
        response.setDetail("Enrollment status for course ID: " + courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/enhanced-status/{courseId}")
    public ResponseEntity<ResponseGeneral<EnrollmentStatusResponseDTO>> getEnhancedEnrollmentStatus(@PathVariable Long courseId) {
        log.info("Get enhanced enrollment status by course id: " + courseId);
        EnrollmentStatusResponseDTO enrollmentStatus = enrollmentService.getEnhancedEnrollmentStatus(courseId);
        ResponseGeneral<EnrollmentStatusResponseDTO> response = new ResponseGeneral<>();
        response.setData(enrollmentStatus);
        response.setMessage("Successfully retrieved enhanced enrollment status");
        response.setDetail("Enhanced enrollment status with role-based access for course ID: " + courseId);
        return ResponseEntity.ok(response);
    }

}
