package com.coursehub.dto.request.lesson;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonConfirmCreationRequestDTO {

    @NotNull(message = "Duration must not be null")
    private Long duration;
}
