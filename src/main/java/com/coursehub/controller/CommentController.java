package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.comment.CommentRequestDTO;
import com.coursehub.dto.response.comment.CommentResponseDTO;
import com.coursehub.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/lesson/{lessonId}")
    public ResponseEntity<ResponseGeneral<CommentResponseDTO>> createComment(
            @PathVariable Long lessonId,
            @RequestBody CommentRequestDTO request
    ) {
        CommentResponseDTO dto = commentService.createComment(lessonId, request);
        ResponseGeneral<CommentResponseDTO> response = new ResponseGeneral<>();
        response.setMessage("Comment created successfully");
        response.setData(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ResponseGeneral<List<CommentResponseDTO>>> getCommentsForLesson(
            @PathVariable Long lessonId
    ) {
        List<CommentResponseDTO> comments = commentService.getCommentsForLesson(lessonId);
        ResponseGeneral<List<CommentResponseDTO>> response = new ResponseGeneral<>();
        response.setMessage("Comments retrieved successfully");
        response.setData(comments);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGeneral<String>> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Comment deleted successfully");
        response.setData("Deleted");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/hide")
    public ResponseEntity<ResponseGeneral<String>> hideComment(@PathVariable Long id) {
        commentService.hideComment(id);
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Comment has been hidden due to violation");
        response.setData("Hidden");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseGeneral<CommentResponseDTO>> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequestDTO request
    ) {
        CommentResponseDTO updated = commentService.updateComment(id, request);
        ResponseGeneral<CommentResponseDTO> response = new ResponseGeneral<>();
        response.setMessage("Update successful");
        response.setData(updated);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<ResponseGeneral<Boolean>> toggleLikeComment(@PathVariable Long id) {
        boolean liked = commentService.toggleLikeComment(id);

        ResponseGeneral<Boolean> response = new ResponseGeneral<>();
        response.setMessage(liked ? "Liked successfully" : "Unliked successfully");
        response.setData(liked);

        return ResponseEntity.ok(response);
    }

}

