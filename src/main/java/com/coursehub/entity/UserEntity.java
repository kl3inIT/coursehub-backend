package com.coursehub.entity;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coursehub.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String password;

    @Column(nullable = false)
    private String name;

    @Column
    private String phone;

    @Column
    private String googleAccountId;

    @Column
    private String avatar;

    @Column
    private String address;

    @Column
    private String gender;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column
    private String bio;

    @Column(name = "is_active")
    @Enumerated(EnumType.STRING)
    private UserStatus isActive = UserStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private RoleEntity roleEntity;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentEntity> commentEntities = new HashSet<>();

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserLessonEntity> userLessonEntities = new HashSet<>();

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EnrollmentEntity> enrollmentEntities = new HashSet<>();

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PaymentEntity> paymentEntities = new HashSet<>();

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CertificateEntity>  certificateEntities = new HashSet<>();

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewEntity> reviewEntities = new HashSet<>();

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseEntity>  courseEntities = new HashSet<>();

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserDiscountEntity>  userDiscountEntities = new HashSet<>();

    @OneToMany(mappedBy = "reporter", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReportEntity> reportsMade = new HashSet<>();

    @OneToMany(mappedBy = "reportedUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReportEntity> reportsReceived = new HashSet<>();

    @OneToMany(mappedBy = "resolvedBy", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReportEntity> reportsResolved = new HashSet<>();

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NotificationEntity> notificationEntities = new HashSet<>();

    @Column(name = "action_reason", columnDefinition = "TEXT")
    private String actionReason;

    @Column(name = "last_action_at")
    private Date lastActionAt;

    @Column(name = "warning_count")
    private Long warningCount = 0L;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AnnouncementUserReadEntity> announcementUserReadEntities = new HashSet<>();

}
