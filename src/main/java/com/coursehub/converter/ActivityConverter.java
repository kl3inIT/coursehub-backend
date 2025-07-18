package com.coursehub.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.coursehub.dto.response.user.UserActivityDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.LessonEntity;
import com.coursehub.entity.ModuleEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.UserActivityType;

@Component
public class ActivityConverter {

    public List<UserActivityDTO> assembleFromUser(UserEntity user) {
        List<UserActivityDTO> activities = new ArrayList<>();
        String roleCode = user.getRoleEntity().getCode().toLowerCase();

        // Manager-specific activities
        if ("manager".equals(roleCode)) {
            addManagerActivities(user, activities);
        }

        // Common activities for all roles
        addEnrollmentActivities(user, activities);
        addCommentActivities(user, activities);
        addLessonCompletionActivities(user, activities);
        addCourseCompletionActivities(user, activities);

        return activities;
    }

    private void addManagerActivities(UserEntity user, List<UserActivityDTO> activities) {
        if (user.getCourseEntities() != null) {
            for (CourseEntity c : user.getCourseEntities()) {
                // Course creation activity
                activities.add(createActivityForCourse(c, UserActivityType.COURSE_CREATION, c.getCreatedDate(), "Created course"));

                // Course update activity
                if (!c.getCreatedDate().equals(c.getModifiedDate())) {
                    activities.add(createActivityForCourse(c, UserActivityType.COURSE_UPDATE, c.getModifiedDate(), "Updated course"));
                }

                // Lesson activities
                addLessonActivities(c, activities);
            }
        }
    }

    private void addLessonActivities(CourseEntity course, List<UserActivityDTO> activities) {
        if (course.getModuleEntities() != null) {
            for (ModuleEntity m : course.getModuleEntities()) {
                if (m.getLessonEntities() != null) {
                    for (LessonEntity l : m.getLessonEntities()) {
                        activities.add(createActivityForLesson(l, UserActivityType.LESSON_CREATION, l.getCreatedDate(), "Created lesson"));
                        if (!l.getCreatedDate().equals(l.getModifiedDate())) {
                            activities.add(createActivityForLesson(l, UserActivityType.LESSON_UPDATE, l.getModifiedDate(), "Updated lesson"));
                        }
                    }
                }
            }
        }
    }

    private void addEnrollmentActivities(UserEntity user, List<UserActivityDTO> activities) {
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
                activity.setProgressPercentage(enrollment.getProgressPercentage());
                activities.add(activity);
            });
        }
    }

    private void addCommentActivities(UserEntity user, List<UserActivityDTO> activities) {
        if (user.getCommentEntities() != null) {
            user.getCommentEntities().forEach(comment -> {
                try {
                    LessonEntity lesson = comment.getLessonEntity();
                    CourseEntity course = lesson.getModuleEntity().getCourseEntity();
                    UserActivityDTO activity = new UserActivityDTO();

                    activity.setId(comment.getId());
                    activity.setType(UserActivityType.COMMENT);
                    activity.setTimestamp(comment.getCreatedDate());
                    activity.setLessonId(lesson.getId());
                    activity.setLessonTitle(lesson.getTitle());
                    activity.setCommentText(comment.getComment());
                    activity.setCourseId(course.getId());
                    activity.setCourseTitle(course.getTitle());
                    activity.setCourseThumbnail(course.getThumbnail());
                    activities.add(activity);
                } catch (Exception e) {
                    // Ignore malformed comment data
                }
            });
        }
    }

    private void addLessonCompletionActivities(UserEntity user, List<UserActivityDTO> activities) {
        if (user.getUserLessonEntities() != null) {
            user.getUserLessonEntities().stream()
                .filter(userLesson -> userLesson.getIsCompleted() == 1L)
                .forEach(userLesson -> {
                    try {
                        LessonEntity lesson = userLesson.getLessonEntity();
                        CourseEntity course = lesson.getModuleEntity().getCourseEntity();
                        activities.add(createActivityForLesson(lesson, UserActivityType.LESSON_COMPLETION, userLesson.getModifiedDate(), null));
                    } catch (Exception e) {
                        // Ignore malformed lesson completion data
                    }
                });
        }
    }

    private void addCourseCompletionActivities(UserEntity user, List<UserActivityDTO> activities) {
        if (user.getEnrollmentEntities() != null) {
            user.getEnrollmentEntities().stream()
                .filter(enrollment -> enrollment.getIsCompleted() == 1L)
                .forEach(enrollment -> {
                    try {
                        CourseEntity course = enrollment.getCourseEntity();
                        UserActivityDTO activity = createActivityForCourse(course, UserActivityType.COURSE_COMPLETION, enrollment.getCompletedDate(), "Course completed");
                        activity.setId(enrollment.getId() + 10000L); // Ensure unique ID
                        activity.setProgressPercentage(100.0);
                        if (enrollment.getCompletedDate() != null) {
                            activity.setActionDescription("Completed at " + enrollment.getCompletedDate());
                        }
                        activities.add(activity);
                    } catch (Exception e) {
                        // Ignore malformed course completion data
                    }
                });
        }
    }

    private UserActivityDTO createActivityForCourse(CourseEntity course, UserActivityType type, java.util.Date timestamp, String description) {
        UserActivityDTO activity = new UserActivityDTO();
        activity.setId(course.getId());
        activity.setType(type);
        activity.setTimestamp(timestamp);
        activity.setCourseId(course.getId());
        activity.setCourseTitle(course.getTitle());
        activity.setCourseThumbnail(course.getThumbnail());
        activity.setActionDescription(description);
        return activity;
    }

    private UserActivityDTO createActivityForLesson(LessonEntity lesson, UserActivityType type, java.util.Date timestamp, String description) {
        UserActivityDTO activity = new UserActivityDTO();
        CourseEntity course = lesson.getModuleEntity().getCourseEntity();
        activity.setId(lesson.getId());
        activity.setType(type);
        activity.setTimestamp(timestamp);
        activity.setCourseId(course.getId());
        activity.setCourseTitle(course.getTitle());
        activity.setLessonId(lesson.getId());
        activity.setLessonTitle(lesson.getTitle());
        activity.setActionDescription(description);
        return activity;
    }
} 