package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.auth.AuthenticationRequestDTO;
import com.coursehub.dto.request.auth.TokenRequestDTO;
import com.coursehub.dto.request.user.OtpRequestDTO;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.auth.AuthenticationResponseDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.service.AuthenticationService;
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
    public ResponseEntity<ResponseGeneral<String>> initUser( @RequestBody UserRequestDTO user) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.initUser(user));
        return ResponseEntity.ok(responseDTO);
    }


    @PostMapping("/register/verify")
    public ResponseEntity<ResponseGeneral<UserResponseDTO>> verifyUser(@RequestBody OtpRequestDTO otpRequest) {
        ResponseGeneral<UserResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.verifyUser(otpRequest));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/register/re-send-otp")
    public ResponseEntity<ResponseGeneral<String>> reSendOtp(@RequestBody OtpRequestDTO otpRequest) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(authenticationService.reSendOtp(otpRequest));
        return ResponseEntity.ok(responseDTO);
    }







}