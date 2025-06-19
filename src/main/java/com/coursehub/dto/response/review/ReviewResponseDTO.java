package com.coursehub.dto.response.review;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    // can remove
    private String userAvatar;
    private Long courseId;
    private String courseName;
    private String categoryName;
    private Integer star;
    private String comment;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
} 