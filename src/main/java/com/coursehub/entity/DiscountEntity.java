package com.coursehub.entity;

import jakarta.persistence.*;
import lombok.Builder;
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
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "percentage", nullable = false)
    private Double percentage;

    @Column(name = "expiry_date")
    private Date expiryDate;
    
    @Column(name = "description")
    private String description;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "is_global", nullable = false)
    private Long isGlobal = 0L;

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
