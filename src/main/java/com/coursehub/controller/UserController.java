package com.coursehub.controller;

import com.coursehub.dto.ResponseDTO;
import com.coursehub.dto.request.user.OtpRequestDTO;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //@Valid de validate du lieu dau vao -> kiem tra requestDTO la biet

    @PostMapping("/register/init")
    public ResponseEntity<ResponseDTO<String>> initUser(@Valid @RequestBody UserRequestDTO user) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(userService.initUser(user));
        return ResponseEntity.ok(responseDTO);
    }


    @PostMapping("/register/verify")
    public ResponseEntity<ResponseDTO<UserResponseDTO>> verifyUser(@RequestBody OtpRequestDTO otpRequest) {
        ResponseDTO<UserResponseDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(userService.verifyUser(otpRequest));
        return ResponseEntity.ok(responseDTO);
    }


}
