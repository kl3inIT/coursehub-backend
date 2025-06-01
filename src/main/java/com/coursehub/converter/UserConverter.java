package com.coursehub.converter;

import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.response.user.UserManagementDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.UserEntity;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final ModelMapper modelMapper;

    //RequestDTO => Entity
    public UserEntity toUserEntity(UserRequestDTO userRequestDTO) {
        return modelMapper.map(userRequestDTO, UserEntity.class);
    }

    //Entity => ResponseDTO
    public UserResponseDTO toUserResponseDTO(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserResponseDTO.class);
    }

    //Entity => ProfileRequestDTO
    public ProfileRequestDTO toProfileRequestDTO(UserEntity userEntity) {
        return modelMapper.map(userEntity, ProfileRequestDTO.class);
    }

    //Entity => UserManagementDTO
    public UserManagementDTO convertToUserManagementDTO(UserEntity user) {
        UserManagementDTO dto = new UserManagementDTO();

        String roleCode = user.getRoleEntity().getCode().isEmpty() ?
                "LEARNER" :
                user.getRoleEntity().getCode().toLowerCase(); // Trong frontend component RoleBadge đang dùng switch case chữ thường

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setRole(roleCode);
        dto.setStatus(user.getIsActive() == 1L ? "active" : "inactive");
        dto.setJoinDate(user.getCreatedDate());

        return dto;
    }

}
