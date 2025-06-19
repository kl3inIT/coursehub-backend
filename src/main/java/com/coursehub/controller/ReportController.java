package com.coursehub.controller;

import static com.coursehub.constant.Constant.CommonConstants.SUCCESS;

import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.report.ReportRequestDTO;
import com.coursehub.dto.request.report.ReportStatusDTO;
import com.coursehub.dto.response.report.ReportResponseDTO;
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
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(reportService.createReport(reportRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<ResponseGeneral<Page<ReportResponseDTO>>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) ResourceType type,
            @RequestParam(required = false) ReportSeverity severity,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) String search) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        ResponseGeneral<Page<ReportResponseDTO>> responseDTO = new ResponseGeneral<>();
        responseDTO.setData(reportService.getAllReports(type, severity, status, search, pageRequest));
        responseDTO.setMessage(SUCCESS);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseGeneral<ReportResponseDTO>> getReportById(@PathVariable Long id) {
        ResponseGeneral<ReportResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(reportService.getReportById(id));
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseGeneral<ReportResponseDTO>> updateReportStatus(
            @PathVariable Long id,
            @RequestBody ReportStatusDTO statusDTO) {
        ResponseGeneral<ReportResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(reportService.updateReportStatus(id, statusDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGeneral<Void>> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        ResponseGeneral<Void> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        return ResponseEntity.ok(responseDTO);
    }
}
