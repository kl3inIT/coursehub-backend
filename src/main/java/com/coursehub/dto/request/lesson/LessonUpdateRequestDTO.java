package com.coursehub.dto.request.lesson;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonUpdateRequestDTO {

    private String title;
    private String description;
    private Long duration;
    private Long order;
    private Boolean isPreview;
}
