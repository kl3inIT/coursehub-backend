package com.coursehub.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coursehub.entity.CommentLikeEntity;

import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {
    Optional<CommentLikeEntity> findByComment_IdAndUser_Id(Long commentId, Long userId);
    List<CommentLikeEntity> findByComment_IdIn(Set<Long> commentIds);
}

