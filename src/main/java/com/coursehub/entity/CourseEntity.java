package com.coursehub.entity;

import com.coursehub.enums.CourseLevel;
import com.coursehub.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseEntity extends BaseEntity {

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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.DRAFT;

    // Relationship with reviews
    @OneToMany(mappedBy = "courseEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewEntity> reviewEntities;

    // Relationship with enrollments
    @OneToMany(mappedBy = "courseEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EnrollmentEntity> enrollmentEntities;

    @OneToMany(mappedBy = "courseEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ModuleEntity> moduleEntities = new HashSet<>();

    @OneToMany(mappedBy = "courseEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseProgressEntity> courseProgressEntities = new HashSet<>();

    @OneToMany(mappedBy = "courseEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PaymentEntity> paymentEntities = new HashSet<>();

    @OneToMany(mappedBy = "courseEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CertificateEntity> certificateEntities = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity categoryEntity;

}

