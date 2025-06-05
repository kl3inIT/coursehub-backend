package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.auth.*;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.auth.AuthenticationResponseDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    //@Valid de validate du lieu dau vao -> kiem tra requestDTO la biet

    @PostMapping("/login")
    public ResponseEntity<ResponseGeneral<AuthenticationResponseDTO>> login(@Valid @RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        ResponseGeneral<AuthenticationResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.login(authenticationRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseGeneral<String>> logout(@Valid @RequestBody TokenRequestDTO tokenRequestDTO) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.logout(tokenRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/register/init")
    public ResponseEntity<ResponseGeneral<String>> initUser(@Valid @RequestBody UserRequestDTO user) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.initUser(user));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/register/verify")
    public ResponseEntity<ResponseGeneral<UserResponseDTO>> verifyUser(@Valid @RequestBody OtpRequestDTO otpRequest) {
        ResponseGeneral<UserResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.verifyUser(otpRequest));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/register/re-send-otp")
    public ResponseEntity<ResponseGeneral<String>> reSendOtp(@Valid @RequestBody OtpRequestDTO otpRequest) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.reSendOtp(otpRequest));
        return ResponseEntity.ok(responseDTO);
    }


    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<ResponseGeneral<String>> sendOtpToResetPassword(@Valid @RequestBody OtpRequestDTO otpRequest) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.sendOtpToResetPassword(otpRequest));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ResponseGeneral<String>> verifyOtpToResetPassword(@Valid @RequestBody OtpRequestDTO otpRequest) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.verifyOtpToResetPassword(otpRequest));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/forgot-password/reset-password")
    public ResponseEntity<ResponseGeneral<String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.resetPassword(resetPasswordRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/google-login-url")
    public ResponseEntity<ResponseGeneral<String>> googleUrl() {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.generateGoogleUrl());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/google/callback")
    public ResponseEntity<ResponseGeneral<AuthenticationResponseDTO>> handCodeFromGoogle(@RequestBody GoogleCodeRequestDTO googleCodeRequestDTO) throws IOException {
        AuthenticationRequestDTO data = authenticationService.handleGoogleCode(googleCodeRequestDTO);
        return this.login(data);
    }


}