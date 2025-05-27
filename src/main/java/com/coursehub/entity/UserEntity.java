package com.coursehub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name")
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false))
    private List<RoleEntity> roles = new ArrayList<>();

    // Relationship with courses (as instructor)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<CourseEntity> courses = new HashSet<>();

    // Relationship with reviews
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<ReviewEntity> reviews = new HashSet<>();

    // Relationship with enrollments
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<EnrollmentEntity> enrollments = new HashSet<>();
}
