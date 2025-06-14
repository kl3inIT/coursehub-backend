package com.coursehub.dto.request.report;

import com.coursehub.enums.ReportStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportStatusDTO {
    private ReportStatus status;
    private String actionNote;
}