package com.coursehub.controller;


import com.coursehub.dto.ResponseGeneral;
import com.coursehub.service.CertificateService;
import com.coursehub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/certificates")
@RequiredArgsConstructor
@Slf4j
public class CertificateController {

    private final CertificateService certificateService;
    private final UserService userService;


    @GetMapping("/count")
    public ResponseEntity<ResponseGeneral<Long>> countByCourseEntity() {
        Long userId = userService.getMyInfo().getId();
        Long totalCourseComplete = certificateService.countByUserEntityId(userId);
        ResponseGeneral<Long> response = new ResponseGeneral<>();
        response.setData(totalCourseComplete);
        response.setMessage("Success count totalCourseComplete");
        response.setDetail("Total complete course: " + totalCourseComplete);
        return ResponseEntity.ok(response);
    }
}
