package com.coursehub.service;

import com.coursehub.dto.request.auth.AuthenticationRequestDTO;
import com.coursehub.dto.request.auth.TokenRequestDTO;
import com.coursehub.dto.request.user.OtpRequestDTO;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.auth.AuthenticationResponseDTO;
import com.coursehub.dto.response.user.UserResponseDTO;

public interface AuthenticationService {
    AuthenticationResponseDTO login(AuthenticationRequestDTO authenticationRequestDTO);
    String logout(TokenRequestDTO tokenRequestDTO);
    boolean verifyToken (String token);
    String initUser(UserRequestDTO userDTO);
    UserResponseDTO verifyUser(OtpRequestDTO otpRequestDTO);
    String reSendOtp(OtpRequestDTO otpRequestDTO);
}
