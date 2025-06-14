package com.coursehub.service;

import com.coursehub.dto.request.report.ReportRequestDTO;
import com.coursehub.dto.request.report.ReportStatusDTO;
import com.coursehub.dto.response.report.ReportResponseDTO;
import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    ReportResponseDTO createReport(ReportRequestDTO reportRequestDTO);
    
    Page<ReportResponseDTO> getAllReports(ResourceType type, ReportSeverity severity, ReportStatus status, String search, Pageable pageable);
    
    ReportResponseDTO getReportById(Long reportId);
    
    ReportResponseDTO updateReportStatus(Long reportId, ReportStatusDTO statusDTO);
    
    void deleteReport(Long reportId);
}
