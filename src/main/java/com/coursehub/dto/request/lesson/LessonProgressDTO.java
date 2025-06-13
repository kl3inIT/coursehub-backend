package com.coursehub.dto.request.lesson;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgressDTO {
    private Long lessonId;
    private Long currentTime;
    private Long watchedTime;
    private Long isCompleted;
}
