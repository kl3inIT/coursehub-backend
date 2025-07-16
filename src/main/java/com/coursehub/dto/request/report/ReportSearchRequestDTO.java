package com.coursehub.dto.request.report;

import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportSearchRequestDTO {

    private Integer page = 0;
    private Integer size = 5;
    private String sortBy = "createdAt";
    private String sortDir = "desc";

    @Size(max = 10, message = "Resource type must be less than 10 characters")
    private ResourceType type;

    @Size(max = 10, message = "Severity must be less than 10 characters")
    private ReportSeverity severity;

    @Size(max = 10, message = "Status must be less than 10 characters")
    private ReportStatus status;

    @Size(max = 100, message = "Search term must be less than 100 characters")
    private String search;

    private Long resourceId;
}
