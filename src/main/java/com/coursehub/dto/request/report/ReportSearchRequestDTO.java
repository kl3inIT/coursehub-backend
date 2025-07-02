package com.coursehub.dto.request.report;

import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;
import lombok.Data;

@Data
public class ReportSearchRequestDTO {
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
    private ResourceType type;
    private ReportSeverity severity;
    private ReportStatus status;
    private String search;
    private Long resourceId;
}
