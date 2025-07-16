package com.coursehub.dto.request.report;

import com.coursehub.enums.ReportStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportStatusDTO {

    @Size(max = 10, message = "Status must be less than 10 characters")
    private ReportStatus status;

    @Size(max = 500, message = "Action note must be less than 100 characters")
    private String actionNote;
}