package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.response.dashboard.DashboardManagerResponseDTO.TopCourse;
import com.coursehub.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

import static com.coursehub.constant.Constant.CommonConstants.SUCCESS;
import com.coursehub.dto.response.dashboard.DashboardManagerResponseDTO;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/manager")
    public ResponseEntity<ResponseGeneral<DashboardManagerResponseDTO>> getManagerDashboard() {
        DashboardManagerResponseDTO data = dashboardService.getManagerDashboard();
        ResponseGeneral<DashboardManagerResponseDTO> response = new ResponseGeneral<>();
        response.setData(data);
        response.setMessage(SUCCESS);
        response.setDetail("Dashboard data retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 