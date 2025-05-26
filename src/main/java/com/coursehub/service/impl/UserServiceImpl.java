package com.coursehub.service.impl;

import com.coursehub.converter.UserConverter;
import com.coursehub.dto.request.user.OtpRequestDTO;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.UserEntity;
import com.coursehub.entity.UserRoleEntity;
import com.coursehub.exception.auth.*;
import com.coursehub.repository.RoleRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.components.OtpUtil;
import com.coursehub.service.UserService;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpUtil otpUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String initUser(UserRequestDTO userDTO) {
        if(userRepository.findByEmailAndIsActive(userDTO.getEmail(), 1L) != null){
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())){
            throw new PasswordNotMatchException("Password not match");
        }

        // luu tam user vao redis
        saveToRedis("user:" + userDTO.getEmail(), userDTO);

        // tao va gui otp
        String otp = otpUtil.generateOtp();
        saveToRedis("otp:" + userDTO.getEmail(), otp);
        otpUtil.sendOtpEmail(userDTO.getEmail(), otp);
        return "Otp is sent to " + userDTO.getEmail();

    }

    @Override
    public UserResponseDTO verifyUser(OtpRequestDTO otpRequestDTO) {
        String storedOtp = (String) getFromRedis("otp:" + otpRequestDTO.getEmail());

        if (storedOtp == null) {
            throw new OtpNotFoundException("Otp not found");
        }
        if (!storedOtp.equals(otpRequestDTO.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }
        UserRequestDTO userRequestDTO = (UserRequestDTO) getFromRedis("user:" + otpRequestDTO.getEmail());
        UserEntity  userEntity = userConverter.toUserEntity(userRequestDTO);
        String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
        userEntity.setPassword(encodedPassword);
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setUserEntity(userEntity);
        userRoleEntity.setRoleEntity(roleRepository.findByCode("LEARNER"));
        userEntity.setUserRoleEntityList(Collections.singleton(userRoleEntity));
        userRepository.save(userEntity);
        return userConverter.toUserResponseDTO(userEntity);
    }

    @Override
//    @PostAuthorize("hasRole('ADMIN') or returnObject.email == authentication.name")
    public UserResponseDTO getUser(long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
        return userConverter.toUserResponseDTO(userEntity);
    }

    @Override
    public UserResponseDTO getMyInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmailAndIsActive(email, 1L);
        if(userEntity == null){
            throw new DataNotFoundException("Data not found");
        }
        return userConverter.toUserResponseDTO(userEntity);
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
