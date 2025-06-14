package com.coursehub.dto.response.lesson;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class LessonResponseDTO {
    private Long id;
    private String title;
    private Long duration;
    private Long orderNumber;
    private Long isPreview;

}
