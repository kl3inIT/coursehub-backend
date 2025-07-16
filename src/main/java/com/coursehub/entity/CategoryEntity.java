package com.coursehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class CategoryEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    @Size(max = 30, message = "Name must be at most 30 characters")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 200, message = "Description must be at most 200 characters")
    private String description;

    @OneToMany(mappedBy = "categoryEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseEntity> courseEntities = new HashSet<>();

    @OneToMany(mappedBy = "categoryEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CategoryDiscountEntity> categoryDiscountEntities = new HashSet<>();
}
