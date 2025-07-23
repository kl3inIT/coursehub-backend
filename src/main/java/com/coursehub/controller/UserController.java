package com.coursehub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.user.ChangePasswordRequestDTO;
import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.response.user.UserDetailDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/myInfo")
    public ResponseEntity<ResponseGeneral<UserResponseDTO>> getMyInfo() {
        ResponseGeneral<UserResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Get My Info Successfully");
        responseDTO.setData(userService.getMyInfo());
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/profile")
    public ResponseEntity<ResponseGeneral<ProfileRequestDTO>> updateProfile(
            @Valid @ModelAttribute ProfileRequestDTO request
    ) {
        ResponseGeneral<ProfileRequestDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(userService.updateProfile(request));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseGeneral<UserDetailDTO>> getUserProfile(@PathVariable Long userId) {
        ResponseGeneral<UserDetailDTO> response = new ResponseGeneral<>();
        UserDetailDTO userDetails = userService.getUserDetails(userId);
        response.setData(userDetails);
        response.setMessage("User profile retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseGeneral<ChangePasswordRequestDTO>> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        ResponseGeneral<ChangePasswordRequestDTO> responseDTO = new ResponseGeneral<>();
        userService.changePassword(request);
        responseDTO.setMessage("Password changed successfully");
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/discounts/{discountId}")
    public ResponseEntity<ResponseGeneral<String>> getCoupon(@PathVariable Long discountId) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Coupon received successfully");
        responseDTO.setData(userService.getDiscount(discountId));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/count")
    public ResponseEntity<ResponseGeneral<Long>> countUser() {
        Long totalUser = userService.countUsers();
        ResponseGeneral<Long> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Total users : " + totalUser);
        responseDTO.setData(totalUser);
        responseDTO.setDetail("Total users : " + totalUser);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/active")
    public ResponseEntity<ResponseGeneral<Long>> countUsersActive() {
        Long totalUserActive = userService.countUserIsActive();
        ResponseGeneral<Long> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Total users : " + totalUserActive);
        responseDTO.setData(totalUserActive);
        responseDTO.setDetail("Total users active: " + totalUserActive);
        return ResponseEntity.ok(responseDTO);
    }

}
