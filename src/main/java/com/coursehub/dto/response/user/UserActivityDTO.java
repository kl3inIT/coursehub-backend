package com.coursehub.dto.response.user;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserActivityDTO {
    private Long id;
    private String type;  // "comment", "enrollment", "course_creation", "course_update"
    private Date timestamp;
    
    // Course info
    private Long courseId;
    private String courseTitle;
    private String courseThumbnail;
    
    // For enrolled courses
    private Double progressPercentage; 
    
    // For comments
    private Long lessonId;     
    private String lessonTitle;      
    private String commentText;    
    
    // For course management (managers only)
    private String actionDescription;  
} 