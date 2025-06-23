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
import com.coursehub.dto.response.user.UserManagementDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.LessonEntity;
import com.coursehub.entity.ModuleEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.UserActivityType;

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
                user.getRoleEntity().getCode().toLowerCase();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setRole(roleCode);
        dto.setStatus(user.getIsActive());
        dto.setJoinDate(user.getCreatedDate());
        dto.setBio(user.getBio());

        List<UserActivityDTO> activities = new ArrayList<>();

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
            dto.setManagedCourses(null);
        } else if ("manager".equals(roleCode)) {
            // Managed courses
            List<CourseBasicDTO> managedCourses = new ArrayList<>();
            if (user.getCourseEntities() != null) {
                for (CourseEntity c : user.getCourseEntities()) {
                    managedCourses.add(new CourseBasicDTO(c.getId(), c.getTitle(), c.getThumbnail()));

                    // Tạo activity cho tạo course
                    UserActivityDTO createActivity = new UserActivityDTO();
                    createActivity.setId(c.getId());
                    createActivity.setType(UserActivityType.COURSE_CREATION);
                    createActivity.setTimestamp(c.getCreatedDate());
                    createActivity.setCourseId(c.getId());
                    createActivity.setCourseTitle(c.getTitle());
                    createActivity.setCourseThumbnail(c.getThumbnail());
                    createActivity.setActionDescription("Created course");
                    activities.add(createActivity);

                    // Nếu có sửa course
                    if (!c.getCreatedDate().equals(c.getModifiedDate())) {
                        UserActivityDTO updateActivity = new UserActivityDTO();
                        updateActivity.setId(c.getId());
                        updateActivity.setType(UserActivityType.COURSE_UPDATE);
                        updateActivity.setTimestamp(c.getModifiedDate());
                        updateActivity.setCourseId(c.getId());
                        updateActivity.setCourseTitle(c.getTitle());
                        updateActivity.setCourseThumbnail(c.getThumbnail());
                        updateActivity.setActionDescription("Updated course");
                        activities.add(updateActivity);
                    }

                    // Tạo/sửa lesson
                    if (c.getModuleEntities() != null) {
                        for (ModuleEntity m : c.getModuleEntities()) {
                            if (m.getLessonEntities() != null) {
                                for (LessonEntity l : m.getLessonEntities()) {
                                    // Tạo lesson
                                    UserActivityDTO lessonCreate = new UserActivityDTO();
                                    lessonCreate.setId(l.getId());
                                    lessonCreate.setType(UserActivityType.LESSON_CREATION);
                                    lessonCreate.setTimestamp(l.getCreatedDate());
                                    lessonCreate.setCourseId(c.getId());
                                    lessonCreate.setCourseTitle(c.getTitle());
                                    lessonCreate.setLessonId(l.getId());
                                    lessonCreate.setLessonTitle(l.getTitle());
                                    lessonCreate.setActionDescription("Created lesson");
                                    activities.add(lessonCreate);

                                    // Sửa lesson
                                    if (!l.getCreatedDate().equals(l.getModifiedDate())) {
                                        UserActivityDTO lessonUpdate = new UserActivityDTO();
                                        lessonUpdate.setId(l.getId());
                                        lessonUpdate.setType(UserActivityType.LESSON_UPDATE);
                                        lessonUpdate.setTimestamp(l.getModifiedDate());
                                        lessonUpdate.setCourseId(c.getId());
                                        lessonUpdate.setCourseTitle(c.getTitle());
                                        lessonUpdate.setLessonId(l.getId());
                                        lessonUpdate.setLessonTitle(l.getTitle());
                                        lessonUpdate.setActionDescription("Updated lesson");
                                        activities.add(lessonUpdate);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            dto.setManagedCourses(managedCourses);
            dto.setEnrolledCourses(null);
        } else {
            dto.setEnrolledCourses(null);
            dto.setManagedCourses(null);
        }

        // Add enrollment activities with progress
        if (user.getEnrollmentEntities() != null) {
        user.getEnrollmentEntities().forEach(enrollment -> {
            UserActivityDTO activity = new UserActivityDTO();
            CourseEntity course = enrollment.getCourseEntity();
            
            activity.setId(enrollment.getId());
                activity.setType(UserActivityType.ENROLLMENT);
            activity.setTimestamp(enrollment.getCreatedDate());
            
            activity.setCourseId(course.getId());
            activity.setCourseTitle(course.getTitle());
            activity.setCourseThumbnail(course.getThumbnail());
            
            // Calculate and set progress percentage
                activity.setProgressPercentage(enrollment.getProgressPercentage());
            
            activities.add(activity);
        });
        }

        // Add comment activities
        if (user.getCommentEntities() != null) {
        user.getCommentEntities().forEach(comment -> {
            try {
                UserActivityDTO activity = new UserActivityDTO();
                LessonEntity lesson = comment.getLessonEntity();
                ModuleEntity module = lesson.getModuleEntity();
                CourseEntity course = module.getCourseEntity();
                
                activity.setId(comment.getId());
                    activity.setType(UserActivityType.COMMENT);
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
        }

        // Add lesson completion activities
        if (user.getUserLessonEntities() != null) {
            user.getUserLessonEntities().stream()
                .filter(userLesson -> userLesson.getIsCompleted() == 1L) // Only completed lessons
                .forEach(userLesson -> {
                    try {
                        UserActivityDTO activity = new UserActivityDTO();
                        LessonEntity lesson = userLesson.getLessonEntity();
                        ModuleEntity module = lesson.getModuleEntity();
                        CourseEntity course = module.getCourseEntity();
                        
                        activity.setId(userLesson.getId());
                        activity.setType(UserActivityType.LESSON_COMPLETION);
                        activity.setTimestamp(userLesson.getModifiedDate()); // When lesson was completed
                        
                        // Set lesson information
                        activity.setLessonId(lesson.getId());
                        activity.setLessonTitle(lesson.getTitle());
                        
                        // Set course information
                        activity.setCourseId(course.getId());
                        activity.setCourseTitle(course.getTitle());
                        activity.setCourseThumbnail(course.getThumbnail());
                        
                        activities.add(activity);
                    } catch (Exception e) {
                        // Skip malformed data
                    }
                });
        }

        // Add course completion activities
        if (user.getEnrollmentEntities() != null) {
            user.getEnrollmentEntities().stream()
                .filter(enrollment -> enrollment.getIsCompleted() == 1L) // Only completed courses
                .forEach(enrollment -> {
                    try {
                        UserActivityDTO activity = new UserActivityDTO();
                        CourseEntity course = enrollment.getCourseEntity();
                        
                        activity.setId(enrollment.getId() + 10000L); // Unique ID for course completion
                        activity.setType(UserActivityType.COURSE_COMPLETION);
                        activity.setTimestamp(enrollment.getCompletedDate()); // When course was completed
                        
                        // Set course information
                        activity.setCourseId(course.getId());
                        activity.setCourseTitle(course.getTitle());
                        activity.setCourseThumbnail(course.getThumbnail());
                        
                        // Set progress to 100% for completed courses
                        activity.setProgressPercentage(100.0);
                        
                        // Add completion description with date
                        if (enrollment.getCompletedDate() != null) {
                            activity.setActionDescription("Completed at " + enrollment.getCompletedDate());
                        } else {
                            activity.setActionDescription("Course completed");
                        }
                        
                        activities.add(activity);
                    } catch (Exception e) {
                        // Skip malformed data
                    }
                });
        }

        dto.setActivities(activities);
        return dto;
    }

}
