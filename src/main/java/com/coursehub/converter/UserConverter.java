package com.coursehub.converter;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.coursehub.dto.request.auth.AuthenticationRequestDTO;
import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.request.user.UserRequestDTO;
import com.coursehub.dto.response.user.UserActivityDTO;
import com.coursehub.dto.response.user.UserManagementDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.EnrollmentEntity;
import com.coursehub.entity.LessonEntity;
import com.coursehub.entity.ModuleEntity;
import com.coursehub.entity.UserEntity;

import lombok.RequiredArgsConstructor;

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

    //Entity => UserManagementDTO
    public UserManagementDTO convertToUserManagementDTO(UserEntity user) {
        if (user == null) return null;

        UserManagementDTO dto = new UserManagementDTO();

        String roleCode = user.getRoleEntity().getCode().isEmpty() ?
                "LEARNER" :
                user.getRoleEntity().getCode().toLowerCase(); // Trong frontend component RoleBadge đang dùng switch case chữ thường

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setRole(roleCode);
        dto.setStatus(user.getIsActive() == 1L ? "active" : "banned");
        dto.setJoinDate(user.getCreatedDate());
        dto.setBio(user.getBio());

        List<UserActivityDTO> activities = new ArrayList<>();

        // Add enrollment activities with progress
        user.getEnrollmentEntities().forEach(enrollment -> {
            UserActivityDTO activity = new UserActivityDTO();
            CourseEntity course = enrollment.getCourseEntity();
            
            activity.setId(enrollment.getId());
            activity.setType("enrollment");
            activity.setTimestamp(enrollment.getCreatedDate());
            
            activity.setCourseId(course.getId());
            activity.setCourseTitle(course.getTitle());
            activity.setCourseThumbnail(course.getThumbnail());
            
            // Calculate and set progress percentage
            activity.setProgressPercentage(calculateProgress(enrollment));
            
            activities.add(activity);
        });

        // Add comment activities
        user.getCommentEntities().forEach(comment -> {
            try {
                UserActivityDTO activity = new UserActivityDTO();
                LessonEntity lesson = comment.getLessonEntity();
                ModuleEntity module = lesson.getModuleEntity();
                CourseEntity course = module.getCourseEntity();
                
                activity.setId(comment.getId());
                activity.setType("comment");
                activity.setTimestamp(comment.getCreatedDate());
                
                // Set lesson and comment information
                activity.setLessonId(lesson.getId());
                activity.setLessonTitle(lesson.getTitle());
                activity.setCommentText(comment.getComment());
                
                // Set course information
                activity.setCourseId(course.getId());
                activity.setCourseTitle(course.getTitle());
                activity.setCourseThumbnail(course.getThumbnail());
                
                activities.add(activity);
            } catch (Exception e) {
                // Skip
            }
        });

        // Add course management activities for managers
        if ("manager".equalsIgnoreCase(dto.getRole())) {
            user.getCourseProgressEntities().forEach(progress -> {
                UserActivityDTO activity = new UserActivityDTO();
                CourseEntity course = progress.getCourseEntity();
                
                activity.setId(progress.getId());
                activity.setType(progress.getCreatedDate().equals(progress.getModifiedDate()) 
                    ? "course_creation" : "course_update");
                activity.setTimestamp(progress.getModifiedDate());
                
                activity.setCourseId(course.getId());
                activity.setCourseTitle(course.getTitle());
                activity.setCourseThumbnail(course.getThumbnail());
                
                activity.setActionDescription(progress.getCreatedDate().equals(progress.getModifiedDate())
                    ? "Created new course" : "Updated course content");
                
                activities.add(activity);
            });
        }

        dto.setActivities(activities);
        return dto;
    }

    private Double calculateProgress(EnrollmentEntity enrollment) {
        CourseEntity course = enrollment.getCourseEntity();
        long totalLessons = course.getModuleEntities().stream()
            .mapToLong(module -> module.getLessonEntities().size())
            .sum();
            
        if (totalLessons == 0) return 0.0;

        long completedLessons = 0;
        
        return (double) completedLessons / totalLessons * 100;
    }


}
