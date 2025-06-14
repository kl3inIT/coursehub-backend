package com.coursehub.entity;

import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "reports")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ReportEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserEntity reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private UserEntity reportedUser;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private ResourceType type;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportSeverity severity = ReportSeverity.LOW;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private UserEntity resolvedBy;

    @Column(name = "action_note", columnDefinition = "TEXT")
    private String actionNote;

}