package com.coursehub.controller;

import com.coursehub.dto.ResponseDTO;
import com.coursehub.dto.request.auth.AuthenticationRequestDTO;
import com.coursehub.dto.request.auth.TokenRequestDTO;
import com.coursehub.dto.response.auth.AuthenticationResponseDTO;
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


}