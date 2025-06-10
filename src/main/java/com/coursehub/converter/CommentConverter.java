package com.coursehub.converter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

import com.coursehub.dto.response.comment.CommentResponseDTO;
import com.coursehub.entity.CommentEntity;
import com.coursehub.entity.UserEntity;

@Component
public class CommentConverter {

    public CommentResponseDTO toDTO(
            CommentEntity entity,
            UserEntity currentUser,
            long likeCount
    ) {
        if (entity == null) return null;
        
        UserEntity user = entity.getUserEntity();
        boolean isManager = Optional.ofNullable(user.getRoleEntity())
                .map(role -> "MANAGER".equalsIgnoreCase(role.getCode()))
                .orElse(false);

        boolean liked = Optional.ofNullable(entity.getLikes())
                .orElse(Set.of())
                .stream()
                .anyMatch(like -> currentUser != null &&
                        like.getUser() != null &&
                        like.getUser().getId().equals(currentUser.getId()));

        boolean isOwner = currentUser != null && currentUser.getId().equals(user.getId());

        return CommentResponseDTO.builder()
                .id(entity.getId())
                .content(entity.getComment())
                .author(user.getName())
                .avatar(user.getAvatar())
                .createdAt(entity.getCreatedDate())
                .isManager(isManager)
                .isHidden(entity.getIsHidden())
                .likeCount(likeCount)
                .likedByCurrentUser(liked)
                .owner(isOwner)
                .build();
    }

    public CommentResponseDTO toDTOWithReplies(
            CommentEntity entity,
            List<CommentResponseDTO> replies,
            UserEntity currentUser,
            long likeCount
    ) {
        CommentResponseDTO dto = toDTO(entity, currentUser, likeCount);
        if (dto != null) dto.setReplies(replies);
        return dto;
    }
}
