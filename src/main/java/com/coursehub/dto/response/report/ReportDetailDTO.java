package com.coursehub.dto.response.report;

import java.util.Date;

import com.coursehub.enums.ReportSeverity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportDetailDTO {
    private Long reportId;
    private Long reporterId;
    private String reporterName;
    private String reporterAvatar;
    private ReportSeverity severity;
    private String reason;
    private Date createdAt;

} 