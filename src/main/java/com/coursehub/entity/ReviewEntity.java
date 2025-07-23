package com.coursehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity courseEntity;

    @Column(nullable = false)
    private Integer star;

    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Comment must be at most 1000 characters")
    private String comment;

    @Column(name = "is_hidden")
    private Long isHidden = 0L;

} 