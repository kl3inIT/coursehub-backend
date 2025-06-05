package com.coursehub.dto.request.course;

import com.coursehub.enums.CourseLevel;
import com.coursehub.enums.CourseStatus;
import com.coursehub.utils.validator.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateStatusAndLevelRequestDTO {

    @EnumValue(name = "status", enumClass = CourseStatus.class)
    private String status;

    @EnumValue(name = "level", enumClass = CourseLevel.class)
    private String level;

}
