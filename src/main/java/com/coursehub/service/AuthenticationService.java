package com.coursehub.service;

import com.coursehub.dto.request.auth.*;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.auth.AuthenticationResponseDTO;
import com.coursehub.dto.response.user.UserResponseDTO;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponseDTO login(AuthenticationRequestDTO authenticationRequestDTO);
    String logout(TokenRequestDTO tokenRequestDTO);
    boolean verifyToken (String token);
    String initUser(UserRequestDTO userDTO);
    UserResponseDTO verifyUser(OtpRequestDTO otpRequestDTO);
    String reSendOtp(OtpRequestDTO otpRequestDTO);
    String sendOtpToResetPassword(OtpRequestDTO otpRequestDTO);
    String verifyOtpToResetPassword(OtpRequestDTO otpRequestDTO);
    String resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
    String generateGoogleUrl();
    AuthenticationRequestDTO handleGoogleCode(GoogleCodeRequestDTO googleCodeRequestDTO) throws IOException;
}
