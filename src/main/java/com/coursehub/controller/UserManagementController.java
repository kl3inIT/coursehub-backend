package com.coursehub.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.request.user.UpdateStatusRequest;
import com.coursehub.dto.request.user.WarnRequestDTO;
import com.coursehub.dto.response.user.UserDetailDTO;
import com.coursehub.dto.response.user.UserSummaryDTO;
import com.coursehub.enums.ResourceType;
import com.coursehub.enums.UserStatus;
import com.coursehub.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserManagementController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<ResponseGeneral<Page<UserSummaryDTO>>> getAllUsers(
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) UserStatus status
    ) {
        ResponseGeneral<Page<UserSummaryDTO>> response = new ResponseGeneral<>();
        Page<UserSummaryDTO> users = adminService.getAllUsers(pageSize, pageNo, role, status);
        response.setData(users);
        response.setMessage("Users retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/detail")
    public ResponseEntity<ResponseGeneral<UserDetailDTO>> getUserDetails(@PathVariable Long userId) {
        ResponseGeneral<UserDetailDTO> response = new ResponseGeneral<>();
        UserDetailDTO userDetails = adminService.getUserDetails(userId);
        response.setData(userDetails);
        response.setMessage("User details retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/course-stats")
    public ResponseEntity<ResponseGeneral<Integer>> getUserCourseStats(@PathVariable Long userId) {
        ResponseGeneral<Integer> response = new ResponseGeneral<>();
        UserDetailDTO userDetails = adminService.getUserDetails(userId);
        
        int courseCount = 0;
        if (userDetails.getEnrolledCourses() != null) {
            courseCount += userDetails.getEnrolledCourses().size();
        }
        if (userDetails.getManagedCourses() != null) {
            courseCount += userDetails.getManagedCourses().size();
        }

        response.setData(courseCount);
        response.setMessage("Course stats retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-manager")
    public ResponseEntity<ResponseGeneral<UserDetailDTO>> createManager(@RequestBody ProfileRequestDTO request) {
        ResponseGeneral<UserDetailDTO> response = new ResponseGeneral<>();
        UserDetailDTO newManager = adminService.createManager(request);
        response.setData(newManager);
        response.setMessage("Manager created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<ResponseGeneral<Void>> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UpdateStatusRequest request
    ) {
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        adminService.updateUserStatus(userId, request.getStatus(), request.getReason());
        response.setMessage("Update user status successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{managerId}")
    public ResponseEntity<ResponseGeneral<Void>> deleteManager(@PathVariable Long managerId) {
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        adminService.updateUserStatus(managerId, UserStatus.INACTIVE, "");
        response.setMessage("Delete user successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/warn")
    public ResponseEntity<ResponseGeneral<Void>> addWarning(
            @PathVariable Long userId,
            @RequestBody WarnRequestDTO warnRequestDTO) {

        ResponseGeneral<Void> response = new ResponseGeneral<>();
        String resourceTypeStr = warnRequestDTO.getResourceType();
        ResourceType resourceType = ResourceType.valueOf(resourceTypeStr);
        adminService.addWarning(userId, resourceType, warnRequestDTO.getResourceId());
        response.setMessage("Warning added successfully");
        return ResponseEntity.ok(response);
    }

} 