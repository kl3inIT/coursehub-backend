package com.coursehub.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.coursehub.constant.Constant.CommonConstants.SUCCESS;
import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.report.ReportRequestDTO;
import com.coursehub.dto.request.report.ReportSearchRequestDTO;
import com.coursehub.dto.request.report.ReportStatusDTO;
import com.coursehub.dto.response.report.AggregatedReportDTO;
import com.coursehub.dto.response.report.ReportResponseDTO;
import com.coursehub.dto.response.report.ResourceLocationDTO;
import com.coursehub.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ResponseGeneral<ReportResponseDTO>> createReport(@RequestBody ReportRequestDTO reportRequestDTO) {
        ResponseGeneral<ReportResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setData(reportService.createReport(reportRequestDTO));
        responseDTO.setMessage(SUCCESS);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/resource/{resourceId}/status")
    public ResponseEntity<ResponseGeneral<Void>> updateResourceReportsStatus(
            @PathVariable Long resourceId,
            @RequestBody ReportStatusDTO statusDTO) {
        reportService.updateAllReportsStatusByResourceId(resourceId, statusDTO);
        ResponseGeneral<Void> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{reportId}/resource-location")
    public ResponseEntity<ResponseGeneral<ResourceLocationDTO>> getReportResourceLocation(@PathVariable Long reportId) {
        ResponseGeneral<ResourceLocationDTO> response = new ResponseGeneral<>();
        ResourceLocationDTO dto = reportService.getResourceLocationByReportId(reportId);
        response.setData(dto);
        response.setMessage(SUCCESS);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resource-location/{resourceId}")
    public ResponseEntity<ResponseGeneral<ResourceLocationDTO>> getResourceLocationByResourceId(@PathVariable Long resourceId) {
        ResponseGeneral<ResourceLocationDTO> response = new ResponseGeneral<>();
        ResourceLocationDTO dto = reportService.getResourceLocationByResourceId(resourceId);
        response.setData(dto);
        response.setMessage(SUCCESS);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/aggregated")
    public ResponseEntity<ResponseGeneral<Page<AggregatedReportDTO>>> getAggregatedReports(@ModelAttribute ReportSearchRequestDTO searchRequest) {
        Page<AggregatedReportDTO> reports = reportService.getAggregatedReports(searchRequest);
        ResponseGeneral<Page<AggregatedReportDTO>> response = new ResponseGeneral<>();
        response.setData(reports);
        response.setMessage(SUCCESS);
        response.setDetail("Aggregated reports retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/aggregated/{resourceId}")
    public ResponseEntity<ResponseGeneral<AggregatedReportDTO>> getAggregatedReportByResourceId(@PathVariable Long resourceId) {
        AggregatedReportDTO report = reportService.getAggregatedReportByResourceId(resourceId);
        ResponseGeneral<AggregatedReportDTO> response = new ResponseGeneral<>();
        response.setData(report);
        response.setMessage(SUCCESS);
        response.setDetail("Aggregated report retrieved successfully");
        return ResponseEntity.ok(response);
    }

}
