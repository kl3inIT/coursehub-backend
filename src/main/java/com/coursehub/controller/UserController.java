package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.user.ChangePasswordRequestDTO;
import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.response.user.UserManagementDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ResponseGeneral<UserManagementDTO>> getUserProfile(@PathVariable Long userId) {
        ResponseGeneral<UserManagementDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Get User Successfully");
        responseDTO.setData(userService.getUserDetails(userId));
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseGeneral<ChangePasswordRequestDTO>> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        ResponseGeneral<ChangePasswordRequestDTO> responseDTO = new ResponseGeneral<>();
        userService.changePassword(request);
        responseDTO.setMessage("Password changed successfully");
        return ResponseEntity.ok(responseDTO);
    }


}
