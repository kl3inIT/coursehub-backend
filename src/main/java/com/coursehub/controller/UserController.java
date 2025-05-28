package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.user.OtpRequestDTO;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/myInfo")
    public ResponseEntity<ResponseGeneral<UserResponseDTO>> getMyInfo() {
        ResponseGeneral<UserResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(userService.getMyInfo());
        return ResponseEntity.ok(responseDTO);
    }
    
}
