package com.coursehub.dto.request.report;

import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ResourceType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {

    @NotNull
    private ResourceType resourceType;

    @NotNull
    private Long resourceId;

    @NotNull
    private Long reporterId;

    @NotNull
    private Long reportedUserId;

    @NotNull
    private String reason;

    private ReportSeverity severity = ReportSeverity.LOW;

    private String description;
}
