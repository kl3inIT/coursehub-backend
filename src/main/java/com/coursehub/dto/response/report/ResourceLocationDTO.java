package com.coursehub.dto.response.report;

import lombok.*;

@NoArgsConstructor
@Data
public class ResourceLocationDTO {
    private String resourceType;
    private Long resourceId;
    private Long lessonId;
    private String lessonName;
    private Long moduleId;
    private String moduleName;
    private Long courseId;
    private String courseName;
}
