package com.coursehub.converter;


import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private ModelMapper modelMapper;

    //DTO => Entity
    public UserEntity toUserEntity(UserRequestDTO userRequestDTO) {
        return modelMapper.map(userRequestDTO, UserEntity.class);
    }

}
