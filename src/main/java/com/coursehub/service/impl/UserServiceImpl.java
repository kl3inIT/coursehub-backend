package com.coursehub.service.impl;

import com.coursehub.converter.UserConverter;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.UserEntity;
import com.coursehub.exception.auth.*;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

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




}
