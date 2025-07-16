package com.coursehub.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.coursehub.converter.CommentConverter;
import com.coursehub.dto.request.comment.CommentRequestDTO;
import com.coursehub.dto.response.comment.CommentResponseDTO;
import com.coursehub.entity.CommentEntity;
import com.coursehub.entity.CommentLikeEntity;
import com.coursehub.entity.LessonEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.UserStatus;
import com.coursehub.exceptions.comment.CommentNotFoundException;
import com.coursehub.exceptions.comment.CommentTooLongException;
import com.coursehub.exceptions.comment.ParentCommentNotFoundException;
import com.coursehub.exceptions.lesson.LessonNotFoundException;
import com.coursehub.exceptions.user.UserNotFoundException;
import com.coursehub.repository.CommentLikeRepository;
import com.coursehub.repository.CommentRepository;
import com.coursehub.repository.LessonRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.CommentService;
import com.coursehub.service.NotificationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentConverter commentConverter;
    private final NotificationService notificationService;


    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndIsActive(email, UserStatus.ACTIVE);
    }

    @Override
    @Transactional
    public CommentResponseDTO createComment(Long lessonId, CommentRequestDTO request) {
        UserEntity user = getCurrentUser();
        if (user == null) throw new UserNotFoundException("User not found");

        LessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found"));

        CommentEntity parent = getParentIfReply(request.getParentId());
        CommentEntity comment = new CommentEntity();
        comment.setComment(request.getContent());
        comment.setLessonEntity(lesson);
        comment.setUserEntity(user);
        comment.setParent(parent);
        comment.setIsHidden(0L);
        comment.setCreatedDate(new Date());
        comment.setModifiedDate(new Date());

        if (request.getContent().length() > 500) {
            throw new CommentTooLongException("Comment content is too long");
        }
        CommentEntity saved = commentRepository.save(comment);
        // ==== Gửi thông báo nếu là reply ====
        if (parent != null && !user.getId().equals(parent.getUserEntity().getId())) {
            notificationService.notifyReplyComment(
                    parent.getUserEntity().getId(), // người nhận reply
                    user.getId(),                   // người reply
                    parent.getId()
            );
        }

        return commentConverter.toDTO(saved, user, 0L);
    }

    private CommentEntity getParentIfReply(Long parentId) {
        if (parentId == null) return null;

        CommentEntity parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new ParentCommentNotFoundException("Parent comment not found"));
        // Nếu parent đã là reply (có parent khác), thì lấy parent gốc
        if (parent.getParent() != null) {
            return parent.getParent();
        }
        // Nếu parent là comment gốc, trả về nó
        return parent;
    }

    @Override
    public List<CommentResponseDTO> getCommentsForLesson(Long lessonId) {
        UserEntity currentUser = getCurrentUser();

        List<CommentEntity> comments = commentRepository.findByLessonEntity_Id(lessonId);
        if (comments.isEmpty()) return List.of();

        Map<Long, Long> likeCountMap = buildLikeCountMapSimple(comments);
        Map<Long, List<CommentEntity>> grouped = groupByParent(comments);

        return buildCommentTree(grouped, 0L, currentUser, likeCountMap);
    }


    private Map<Long, Long> buildLikeCountMapSimple(List<CommentEntity> comments) {
        if (comments.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<Long> commentIds = comments.stream()
                .map(CommentEntity::getId)
                .collect(Collectors.toSet());

        try {
            List<CommentLikeEntity> likes = commentLikeRepository.findByComment_IdIn(commentIds);

            return likes.stream()
                    .collect(Collectors.groupingBy(
                            like -> like.getComment().getId(),
                            Collectors.counting()
                    ));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private Map<Long, List<CommentEntity>> groupByParent(List<CommentEntity> comments) {
        return comments.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getParent() != null ? c.getParent().getId() : 0L,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()))
                                        .toList()
                        )
                ));
    }

    private List<CommentResponseDTO> buildCommentTree(
            Map<Long, List<CommentEntity>> grouped,
            Long parentId,
            UserEntity currentUser,
            Map<Long, Long> likeCountMap
    ){
        List<CommentEntity> children = grouped.getOrDefault(parentId, Collections.emptyList());
        // Nếu là comment con (parentId != 0), sort replies cũ nhất lên trước (tăng dần theo ngày)
        List<CommentEntity> sortedChildren = (parentId != 0L)
            ? children.stream()
                .sorted((a, b) -> a.getCreatedDate().compareTo(b.getCreatedDate()))
                .toList()
            : children;
        return sortedChildren.stream()
                .map(child -> {
                    List<CommentResponseDTO> replies = buildCommentTree(grouped, child.getId(), currentUser, likeCountMap);
                    Long likeCount = likeCountMap.getOrDefault(child.getId(), 0L);
                    return commentConverter.toDTOWithReplies(child, replies, currentUser, likeCount);
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        UserEntity user = getCurrentUser();
        CommentEntity comment = getCommentOrThrow(commentId);
        if (!comment.getUserEntity().getId().equals(user.getId())) {
            throw new SecurityException("You can only delete your own comment");
        }
        comment.getLikes().clear();
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void setCommentVisibility(Long commentId, boolean hide ) {
        CommentEntity comment = getCommentOrThrow(commentId);
        comment.setIsHidden(hide ? 1L : 0L);
        comment.setModifiedDate(new Date());
        commentRepository.save(comment);
        if (hide) {
            notificationService.notifyHideResource(
                    comment.getUserEntity().getId(),
                    commentId,
                    "COMMENT"
            );
        } else {
            notificationService.notifyShowResource(
                    comment.getUserEntity().getId(),
                    commentId,
                    "COMMENT"
            );
        }
    }

    @Override
    @Transactional
    public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO request) {
        UserEntity user = getCurrentUser();
        CommentEntity comment = getCommentOrThrow(commentId);
        if (!comment.getUserEntity().getId().equals(user.getId())) {
            throw new SecurityException("You can only update your own comment");
        }
        long likeCount = comment.getLikes().size();

        comment.setComment(request.getContent());
        comment.setModifiedDate(new Date());
        return commentConverter.toDTO(commentRepository.save(comment), user, likeCount);
    }

    private CommentEntity getCommentOrThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
    }

    @Override
    @Transactional
    public boolean toggleLikeComment(Long commentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmailAndIsActive(email, UserStatus.ACTIVE);
        if (user == null) throw new UserNotFoundException("User not found");
        CommentEntity comment = getCommentOrThrow(commentId);

        Optional<CommentLikeEntity> existingLike = commentLikeRepository.findByComment_IdAndUser_Id(commentId, user.getId());

        if (existingLike.isPresent()) {
            commentLikeRepository.delete(existingLike.get());
            return false;
        } else {
            CommentLikeEntity like = new CommentLikeEntity();
            like.setComment(comment);
            like.setUser(user);
            like.setCreatedDate(new Date());
            commentLikeRepository.save(like);
            if (!user.getId().equals(comment.getUserEntity().getId())) {
                notificationService.notifyLikeComment(
                        comment.getUserEntity().getId(), // người nhận
                        user.getId(),                    // người like
                        commentId
                );
            }
            return true;
        }
    }

    @Override
    public CommentResponseDTO getCommentById(Long commentId) {
        UserEntity user = getCurrentUser();
        CommentEntity comment = getCommentOrThrow(commentId);
        long likeCount = comment.getLikes().size();
        return commentConverter.toDTO(comment, user, likeCount);
    }

}
