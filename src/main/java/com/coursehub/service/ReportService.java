package com.coursehub.service;

import org.springframework.data.domain.Page;

import com.coursehub.dto.request.report.ReportRequestDTO;
import com.coursehub.dto.request.report.ReportSearchRequestDTO;
import com.coursehub.dto.request.report.ReportStatusDTO;
import com.coursehub.dto.response.report.AggregatedReportDTO;
import com.coursehub.dto.response.report.ReportResponseDTO;
import com.coursehub.dto.response.report.ResourceLocationDTO;
import com.coursehub.enums.ResourceType;

public interface ReportService {
    ReportResponseDTO createReport(ReportRequestDTO reportRequestDTO);

    Page<ReportResponseDTO> searchReports(ReportSearchRequestDTO request);
    
    ReportResponseDTO getReportById(Long reportId);
    
    ReportResponseDTO updateReportStatus(Long reportId, ReportStatusDTO statusDTO);
    
    void deleteReport(Long reportId);

    ResourceLocationDTO getResourceLocation(ResourceType type, Long resourceId);

    ResourceLocationDTO getResourceLocationByReportId(Long reportId);

    ResourceLocationDTO getResourceLocationByResourceId(Long resourceId);

    boolean isAllowedToReport(Long userId);

    // Lấy danh sách report tổng hợp theo resource
    Page<AggregatedReportDTO> getAggregatedReports(ReportSearchRequestDTO searchRequest);
    
    // Lấy một aggregated report theo resourceId
    AggregatedReportDTO getAggregatedReportByResourceId(Long resourceId);
    
    // Cập nhật status của tất cả reports cho một resource
    void updateAllReportsStatusByResourceId(Long resourceId, ReportStatusDTO statusDTO);
}
