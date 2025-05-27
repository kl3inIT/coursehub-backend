package com.coursehub.entity;

import com.coursehub.enums.CourseLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseEntity extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column
    private String title;

    @Column(precision = 8, scale = 2)
    private BigDecimal price;

    @Column(precision = 8, scale = 2)
    private BigDecimal discount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private CourseLevel level;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "duration")
    private Integer duration;

    // Relationship with lessons
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Set<LessonEntity> lessons;

    // Relationship with reviews
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Set<ReviewEntity> reviews;

    // Relationship with enrollments
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Set<EnrollmentEntity> enrollments;
}