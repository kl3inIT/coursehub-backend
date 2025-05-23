package com.coursehub.service.impl;

import com.coursehub.dto.request.user.OtpRequestDTO;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.exception.auth.*;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.OtpService;
import com.coursehub.service.UserService;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String initUser(UserRequestDTO userDTO) {
        if(userRepository.findByEmail(userDTO.getEmail()) != null){
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())){
            throw new PasswordNotMatchException("Password not match");
        }

        // luu tam user vao redis
        saveToRedis(userDTO.getEmail(), userDTO);

        // tao va gui otp
        String otp = otpService.generateOtp();
        saveToRedis(userDTO.getEmail(), otp);
        otpService.sendOtpEmail(userDTO.getEmail(), otp);
        return "Otp is sent to " + userDTO.getEmail();

    }

    @Override
    public UserResponseDTO verifyUser(OtpRequestDTO otpRequestDTO) {
        String storedOtp = (String) getFromRedis(otpRequestDTO.getEmail());

        if (storedOtp == null) {
            throw new OtpNotFoundException("Otp not found");
        }
        if (!storedOtp.equals(otpRequestDTO.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }
        return null;
    }



    public void saveToRedis(String key, Object value) {
        try {
            redisTemplate.opsForValue().set( key, value, 1, TimeUnit.MINUTES);
        } catch (RedisConnectionException e) {
            throw new RedisOperationException("Failed to save " + key + " to Redis", e);
        }
    }

    public Object getFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }


}
