package com.coursehub.service;

import com.coursehub.dto.request.report.ReportRequestDTO;
import com.coursehub.dto.request.report.ReportSearchRequestDTO;
import com.coursehub.dto.request.report.ReportStatusDTO;
import com.coursehub.dto.response.report.ReportResponseDTO;
import com.coursehub.dto.response.report.ResourceLocationDTO;
import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    ReportResponseDTO createReport(ReportRequestDTO reportRequestDTO);

    Page<ReportResponseDTO> searchReports(ReportSearchRequestDTO request);
    
    ReportResponseDTO getReportById(Long reportId);
    
    ReportResponseDTO updateReportStatus(Long reportId, ReportStatusDTO statusDTO);
    
    void deleteReport(Long reportId);

    ResourceLocationDTO getResourceLocation(ResourceType type, Long resourceId);

    ResourceLocationDTO getResourceLocationByReportId(Long reportId);

    boolean isAllowedToReport(Long userId);
}
