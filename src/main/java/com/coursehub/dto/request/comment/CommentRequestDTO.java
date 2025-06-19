package com.coursehub.dto.request.comment;

import lombok.Data;

@Data
public class CommentRequestDTO {
    private String content;
    private Long parentId;    // Nếu null → comment gốc, nếu có → là reply
}

