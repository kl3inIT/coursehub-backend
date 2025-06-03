package com.coursehub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonEntity extends BaseEntity {

    @Column
    private String title;

    @Column(name = "s3_key")
    private String s3Key;

    @Column(name = "order_number")
    private Long orderNumber;

    @Column(name = "is_active")
    @Builder.Default
    private Long isActive = 1L;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "is_preview")
    @Builder.Default
    private Long isPreview = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private ModuleEntity moduleEntity;

    @OneToMany(mappedBy = "lessonEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentEntity> commentEntities = new HashSet<>();

    @OneToMany(mappedBy = "lessonEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserLessonEntity> userLessonEntities = new HashSet<>();

}
