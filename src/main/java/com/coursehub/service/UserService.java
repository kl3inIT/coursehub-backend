package com.coursehub.service;

import com.coursehub.dto.request.user.OtpRequestDTO;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.user.UserResponseDTO;


public interface UserService {

    String initUser(UserRequestDTO userDTO);
    UserResponseDTO verifyUser(OtpRequestDTO otpRequestDTO);
    UserResponseDTO getUser(long userId);
    UserResponseDTO getMyInfo();
}
