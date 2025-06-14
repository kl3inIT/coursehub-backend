package com.coursehub.controller.admin;

import org.springframework.data.domain.Page;
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
import com.coursehub.dto.response.user.UserManagementDTO;
import com.coursehub.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ResponseGeneral<Page<UserManagementDTO>>> getAllUsers(
        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @RequestParam(required = false, defaultValue = "0") Integer pageNo,
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String status
    ) {
        ResponseGeneral<Page<UserManagementDTO>> response = new ResponseGeneral<>();
        response.setData(userService.getAllUsers(pageSize, pageNo, role, status));
        response.setMessage("Get all users successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/detail")
    public ResponseEntity<ResponseGeneral<UserManagementDTO>> getUserDetails(@PathVariable Long userId) {
        ResponseGeneral<UserManagementDTO> response = new ResponseGeneral<>();
        response.setData(userService.getUserDetails(userId));
        response.setMessage("Get user details successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-manager")
    public ResponseEntity<ResponseGeneral<UserManagementDTO>> createManager(@RequestBody ProfileRequestDTO request) {
        ResponseGeneral<UserManagementDTO> response = new ResponseGeneral<>();
        response.setData(userService.createManager(request));
        response.setMessage("Create user successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<ResponseGeneral<Void>> updateUserStatus(
        @PathVariable Long userId,
        @RequestParam String status
    ) {
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        userService.updateUserStatus(userId, status);
        response.setMessage("Update user status successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{managerId}")
    public ResponseEntity<ResponseGeneral<Void>> deleteManager(@PathVariable Long managerId) {
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        userService.deleteManager(managerId);
        response.setMessage("Delete user successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/warn")
    public ResponseEntity<ResponseGeneral<Void>> addWarning(@PathVariable Long userId) {
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        userService.addWarning(userId);
        response.setMessage("Warning added successfully");
        return ResponseEntity.ok(response);
    }
} 