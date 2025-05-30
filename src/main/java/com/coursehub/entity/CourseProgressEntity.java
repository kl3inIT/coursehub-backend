package com.coursehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "course_progress")
@Getter
@Setter
public class CourseProgressEntity extends BaseEntity {


    @Column(name = "total_lesson")
    private Long totalLesson;

    @Column(name = "completed_lesson")
    private Long completedLesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity courseEntity;
}
