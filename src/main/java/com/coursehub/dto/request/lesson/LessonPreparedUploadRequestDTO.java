package com.coursehub.dto.request.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonPreparedUploadRequestDTO {

    @NotBlank
    @Size(min = 3, max = 255, message = "Title length must be between 3 and 255 characters")
    String title;

    @NotBlank
    String fileName;

    @NotBlank
    String fileType;

}
