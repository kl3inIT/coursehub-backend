package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.dto.response.enrollment.EnrollmentResponseDTO;
import com.coursehub.service.EnrollmentService;
import com.coursehub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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


    @GetMapping("/enrolled")
    public ResponseEntity<ResponseGeneral<Page<EnrollmentResponseDTO>>> getByUserEntityId(Pageable pageable) {
        Long userId = userService.getMyInfo().getId();
        log.info("Get enrolled courses by user id: " + userId);
        Page<EnrollmentResponseDTO> enrollmentResponseDTOS = enrollmentService.findByUserEntityId(userId, pageable);
        ResponseGeneral<Page<EnrollmentResponseDTO>> response = new ResponseGeneral<>();
        response.setData(enrollmentResponseDTOS);
        response.setMessage("Successfully count total enrolled courses");
        response.setDetail("Total enrolled courses: " + enrollmentResponseDTOS.getContent().size());
        return ResponseEntity.ok(response);
    }
}
