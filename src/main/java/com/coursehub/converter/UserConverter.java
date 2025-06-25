package com.coursehub.converter;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.coursehub.dto.request.auth.AuthenticationRequestDTO;
import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.course.CourseBasicDTO;
import com.coursehub.dto.response.user.UserActivityDTO;
import com.coursehub.dto.response.user.UserDetailDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final ModelMapper modelMapper;
    private final ActivityConverter activityConverter;

    //RequestDTO => Entity
    public UserEntity toUserEntity(UserRequestDTO userRequestDTO) {
        return modelMapper.map(userRequestDTO, UserEntity.class);
    }

    //Entity => ResponseDTO
    public UserResponseDTO toUserResponseDTO(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserResponseDTO.class);
    }

    public UserEntity toUserEntity(AuthenticationRequestDTO authenticationRequestDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(authenticationRequestDTO.getName());
        userEntity.setEmail(authenticationRequestDTO.getEmail());
        userEntity.setAvatar(authenticationRequestDTO.getAvatar());
        userEntity.setPhone(authenticationRequestDTO.getPhone());
        userEntity.setGoogleAccountId(authenticationRequestDTO.getGoogleAccountId());
        return userEntity;
    }

    //Entity => ProfileRequestDTO
    public ProfileRequestDTO toProfileRequestDTO(UserEntity userEntity) {
        return modelMapper.map(userEntity, ProfileRequestDTO.class);
    }

    //Entity => UserDetailDTO
    public UserDetailDTO toUserDetailDTO(UserEntity user) {
        if (user == null) return null;

        UserDetailDTO dto = new UserDetailDTO();

        String roleCode = user.getRoleEntity().getCode().isEmpty() ?
                "LEARNER" :
                user.getRoleEntity().getCode().toLowerCase();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setRole(roleCode);
        dto.setStatus(user.getIsActive());
        dto.setJoinDate(user.getCreatedDate());
        dto.setBio(user.getBio());

        // Tách riêng cho learner và manager
        if ("learner".equals(roleCode)) {
            // Chỉ set enrolledCourses cho learner
            List<CourseBasicDTO> enrolledCourses = new ArrayList<>();
            if (user.getEnrollmentEntities() != null) {
                user.getEnrollmentEntities().forEach(enrollment -> {
                    CourseEntity c = enrollment.getCourseEntity();
                    enrolledCourses.add(new CourseBasicDTO(c.getId(), c.getTitle(), c.getThumbnail()));
                });
            }
            dto.setEnrolledCourses(enrolledCourses);
            dto.setManagedCourses(new ArrayList<>());
        } else if ("manager".equals(roleCode)) {
            // Managed courses
            List<CourseBasicDTO> managedCourses = new ArrayList<>();
            if (user.getCourseEntities() != null) {
                for (CourseEntity c : user.getCourseEntities()) {
                    managedCourses.add(new CourseBasicDTO(c.getId(), c.getTitle(), c.getThumbnail()));
                }
            }
            dto.setManagedCourses(managedCourses);
            dto.setEnrolledCourses(new ArrayList<>());
        } else {
            dto.setEnrolledCourses(new ArrayList<>());
            dto.setManagedCourses(new ArrayList<>());
        }

        // Delegate activity assembly to ActivityConverter
        List<UserActivityDTO> activities = activityConverter.assembleFromUser(user);
        dto.setActivities(activities);

        return dto;
    }

}
