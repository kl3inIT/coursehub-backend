package com.coursehub.dto.response.comment;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String author;
    private String avatar;
    private Long userId;
    private Date createdAt;
    private boolean isManager;
    private Long isHidden;
    private Long likeCount;
    private boolean likedByCurrentUser;
    private boolean owner;
    private List<CommentResponseDTO> replies;
    private Long lessonId;
}

