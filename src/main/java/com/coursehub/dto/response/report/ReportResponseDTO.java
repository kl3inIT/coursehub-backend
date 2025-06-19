package com.coursehub.dto.response.report;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ReportResponseDTO {
    // Thông tin chung về report
    private Long reportId;
    private String type;
    private String severity;
    private String status;
    private String reason;
    private Long resourceId;
    private String description;
    private String reporterName;
    private String reportedUserName;
    private Long reportedUserId;
    private long warningCount;
    private String reportedUserStatus;
    private String reportedUserMemberSince;
    private boolean hidden;
    private Date createdAt;
    private String actionNote;

}

