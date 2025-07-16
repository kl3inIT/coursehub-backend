package com.coursehub.dto.response.lesson;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class LessonVideoUpdateResponseDTO {
    private Long lessonId;
    private String title;
    private String preSignedPutUrl;
    private String s3Key;
    private String fileName;
    private String fileType;
} 