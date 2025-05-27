package com.coursehub.service.impl;

import com.coursehub.converter.UserConverter;
import com.coursehub.dto.UserDTO;
import com.coursehub.entity.UserEntity;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Override
    public List<UserDTO> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
    }
}
