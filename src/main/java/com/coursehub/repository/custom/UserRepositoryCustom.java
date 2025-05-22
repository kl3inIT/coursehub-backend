package com.coursehub.repository.custom;

import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.entity.UserEntity;

import java.util.List;

public interface UserRepositoryCustom {
    UserEntity createUser(UserRequestDTO userRequestDTO);

    List<UserEntity> findAll();
}
