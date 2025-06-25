package com.coursehub.dto.response.report;

import java.util.List;

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
    private String resourceOwnerMemberSince;
    private Long warningCount;
    private boolean hidden;
    private List<ReportDetailDTO> reports;
} 