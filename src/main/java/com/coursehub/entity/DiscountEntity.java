package com.coursehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "discounts")
@Getter
@Setter
public class DiscountEntity extends BaseEntity {

    @Column(name = "percentage", nullable = false)
    private Double percentage;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;
    
    @Column(name = "description")
    private String description;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "is_active", nullable = false)
    private Long isActive;

    @OneToMany(mappedBy = "discountEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserDiscountEntity> userDiscountEntities = new HashSet<>();

    @OneToMany(mappedBy = "discountEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CategoryDiscountEntity> categoryDiscountEntities = new HashSet<>();

    @OneToMany(mappedBy = "discountEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseDiscountEntity> courseDiscountEntities = new HashSet<>();

    @OneToMany(mappedBy = "discountEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PaymentEntity> paymentEntities = new HashSet<>();

    // Additional fields and methods can be added as needed
}
