package com.coursehub.dto.response.report;

import java.util.Date;
import java.util.List;

import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ReportStatus;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AggregatedReportDTO {
    private Long resourceId;
    private String resourceType;
    private String resourceContent;
    private Long resourceOwnerId;
    private String resourceOwner;
    private String resourceOwnerAvatar;
    private String resourceOwnerStatus;
    private ReportSeverity severity;
    private String resourceOwnerMemberSince;
    private Long warningCount;
    private ReportStatus status;
    private Date createdAt;
    private Long totalReports;
    private boolean hidden;
    private List<ReportDetailDTO> reports;
} 