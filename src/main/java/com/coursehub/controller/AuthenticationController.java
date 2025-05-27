package com.coursehub.controller;

import com.coursehub.dto.ResponseDTO;
import com.coursehub.dto.request.auth.AuthenticationRequestDTO;
import com.coursehub.dto.request.auth.TokenRequestDTO;
import com.coursehub.dto.request.user.OtpRequestDTO;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.auth.AuthenticationResponseDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.service.AuthenticationService;
import com.coursehub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    //@Valid de validate du lieu dau vao -> kiem tra requestDTO la biet

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<AuthenticationResponseDTO>> login(@Valid @RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        ResponseDTO<AuthenticationResponseDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.login(authenticationRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO<String>> logout(@Valid @RequestBody TokenRequestDTO tokenRequestDTO) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.logout(tokenRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/register/init")
    public ResponseEntity<ResponseDTO<String>> initUser( @RequestBody UserRequestDTO user) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.initUser(user));
        return ResponseEntity.ok(responseDTO);
    }


    @PostMapping("/register/verify")
    public ResponseEntity<ResponseDTO<UserResponseDTO>> verifyUser(@RequestBody OtpRequestDTO otpRequest) {
        ResponseDTO<UserResponseDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.verifyUser(otpRequest));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/register/re-send-otp")
    public ResponseEntity<ResponseDTO<String>> reSendOtp(@RequestBody OtpRequestDTO otpRequest) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.reSendOtp(otpRequest));
        return ResponseEntity.ok(responseDTO);
    }







}