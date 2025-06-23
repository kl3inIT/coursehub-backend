package com.coursehub.dto.response.user;

import java.util.Date;

import com.coursehub.enums.UserActivityType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserActivityDTO {
    private Long id;
    private UserActivityType type; 
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
    
    // Helper method to get type as string (for JSON serialization compatibility)
    public String getTypeValue() {
        return type != null ? type.getValue() : null;
    }
    
    // Helper method to set type from string (for deserialization)
    public void setTypeFromString(String typeValue) {
        this.type = typeValue != null ? UserActivityType.fromValue(typeValue) : null;
    }
} 