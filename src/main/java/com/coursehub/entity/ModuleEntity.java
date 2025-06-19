package com.coursehub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "modules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleEntity extends BaseEntity {

    @Column
    private String title;

    @Column(name = "order_number")
    private Long orderNumber;

    @Column(name = "is_active")
    @Builder.Default
    private Long isActive = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private CourseEntity courseEntity;

    @OneToMany(mappedBy = "moduleEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LessonEntity> lessonEntities = new HashSet<>();

}
