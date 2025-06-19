package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.report.ReportRequestDTO;
import com.coursehub.dto.request.report.ReportSearchRequestDTO;
import com.coursehub.dto.request.report.ReportStatusDTO;
import com.coursehub.dto.response.report.ReportResponseDTO;
import com.coursehub.dto.response.report.ResourceLocationDTO;
import com.coursehub.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.coursehub.constant.Constant.CommonConstants.SUCCESS;

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

    @GetMapping
    public ResponseEntity<ResponseGeneral<Page<ReportResponseDTO>>> getAllReports(
            @ModelAttribute ReportSearchRequestDTO searchRequest
    ) {
        Page<ReportResponseDTO> reports = reportService.searchReports(searchRequest);
        ResponseGeneral<Page<ReportResponseDTO>> response = new ResponseGeneral<>();
        response.setData(reports);
        response.setMessage(SUCCESS);
        response.setDetail("Reports retrieved successfully");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseGeneral<ReportResponseDTO>> getReportById(@PathVariable Long id) {
        ResponseGeneral<ReportResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setData(reportService.getReportById(id));
        responseDTO.setMessage(SUCCESS);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseGeneral<ReportResponseDTO>> updateReportStatus(
            @PathVariable Long id,
            @RequestBody ReportStatusDTO statusDTO) {
        ResponseGeneral<ReportResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setData(reportService.updateReportStatus(id, statusDTO));
        responseDTO.setMessage(SUCCESS);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGeneral<Void>> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
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

}
