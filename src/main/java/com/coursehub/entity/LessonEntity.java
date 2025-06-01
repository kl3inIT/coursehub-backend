package com.coursehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lessons")
@Getter
@Setter
public class LessonEntity extends BaseEntity {

    @Column
    private String title;

    @Column(name = "s3_key")
    private String s3_key;

    @Column(name = "order_number")
    private Long orderNumber;

    @Column(name = "is_active")
    private Long isActive = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private ModuleEntity moduleEntity;

    @OneToMany(mappedBy = "lessonEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentEntity> commentEntities = new HashSet<>();

    @OneToMany(mappedBy = "lessonEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserLessonEntity> userLessonEntities = new HashSet<>();

}
