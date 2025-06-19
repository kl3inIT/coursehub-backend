package com.coursehub.service;


import java.util.List;

import com.coursehub.dto.request.comment.CommentRequestDTO;
import com.coursehub.dto.response.comment.CommentResponseDTO;

public interface CommentService {
    CommentResponseDTO createComment(Long lessonId, CommentRequestDTO request);
    List<CommentResponseDTO> getCommentsForLesson(Long lessonId);
    void deleteComment(Long commentId);
    void setCommentVisibility(Long commentId, boolean hide );
    CommentResponseDTO updateComment(Long commentId, CommentRequestDTO request);
    boolean toggleLikeComment(Long commentId);
    CommentResponseDTO getCommentById(Long commentId);
}

