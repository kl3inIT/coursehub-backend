package com.coursehub.repository.custom.impl;

import com.coursehub.converter.UserConverter;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.entity.UserEntity;
import com.coursehub.repository.UserRepository;
import com.coursehub.repository.custom.UserRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final UserConverter userConverter;

    @Override
    public UserEntity createUser(UserRequestDTO userRequestDTO) {
        UserEntity userEntity = userConverter.toUserEntity(userRequestDTO);
//        UserRepository.save(userEntity);
        return userEntity;
    }

    @Override
    public List<UserEntity> findAll() {
        return List.of();
    }

}
