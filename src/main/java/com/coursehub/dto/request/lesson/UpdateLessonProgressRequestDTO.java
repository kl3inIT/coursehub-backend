package com.coursehub.dto.request.lesson;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLessonProgressRequestDTO {

    @NotNull
    private Long currentTime;

    @NotNull
    private Long watchedDelta;
}
