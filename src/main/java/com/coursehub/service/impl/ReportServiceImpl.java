package com.coursehub.service.impl;

import com.coursehub.converter.ReportConverter;
import com.coursehub.dto.request.report.ReportRequestDTO;
import com.coursehub.dto.request.report.ReportStatusDTO;
import com.coursehub.dto.response.report.ReportResponseDTO;
import com.coursehub.entity.CommentEntity;
import com.coursehub.entity.ReportEntity;
import com.coursehub.entity.ReviewEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;
import com.coursehub.exceptions.comment.CommentNotFoundException;
import com.coursehub.exceptions.report.ReportNotFoundException;
import com.coursehub.exceptions.review.ReviewNotFoundException;
import com.coursehub.repository.CommentRepository;
import com.coursehub.repository.ReportRepository;
import com.coursehub.repository.ReviewRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.ReportService;
import com.coursehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ReportConverter reportConverter;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public ReportResponseDTO createReport(ReportRequestDTO dto) {
        UserEntity reporter = getCurrentUser();
        ReportEntity report = new ReportEntity();
        if(dto.getResourceType().equals(ResourceType.COMMENT)) {
            CommentEntity comment = commentRepository.findByIdWithUser(dto.getResourceId()) //Fetch comment để lấy thông tin userEntity
                    .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + dto.getResourceId()));
            UserEntity reportedUser = comment.getUserEntity();
            report.setReportedUser(reportedUser);
        } else {
            ReviewEntity review = reviewRepository.findByIdWithUser(dto.getResourceId())
                    .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + dto.getResourceId()));
            UserEntity reportedUser = review.getUserEntity();
            report.setReportedUser(reportedUser);
        }
        report.setType(dto.getResourceType());
        report.setResourceId(dto.getResourceId());
        report.setReporter(reporter);
        report.setResourceId(dto.getResourceId());
        report.setReason(dto.getReason());
        report.setSeverity(dto.getSeverity() != null ? dto.getSeverity() : ReportSeverity.LOW);
        report.setStatus(ReportStatus.PENDING);
        report.setCreatedDate(new Date());
        reportRepository.save(report);
        return reportConverter.toResponseDTO(report);
    }

    @Override
    public Page<ReportResponseDTO> getAllReports(ResourceType type, ReportSeverity severity, ReportStatus status, String search, Pageable pageable) {
        Page<ReportEntity> reports = reportRepository.findAllWithFilters(type, severity, status, search, pageable);
        return reports.map(reportConverter::toResponseDTO);
    }

    @Override
    public ReportResponseDTO getReportById(Long reportId) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found with ID: " + reportId));
        return reportConverter.toResponseDTO(report);
    }

    @Override
    @Transactional
    public ReportResponseDTO updateReportStatus(Long reportId, ReportStatusDTO statusDTO) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found with ID: " + reportId));

        if (!report.getStatus().equals(ReportStatus.PENDING)) {
            throw new IllegalStateException("Report has already been processed");
        }

        report.setStatus(statusDTO.getStatus());
        report.setActionNote(statusDTO.getActionNote());
        report.setResolvedBy(getCurrentUser());
        report.setModifiedDate(new Date());

        reportRepository.save(report);

        return reportConverter.toResponseDTO(report);
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new ReportNotFoundException("Report not found with ID: " + reportId);
        }
        reportRepository.deleteById(reportId);
    }

    private UserEntity getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        return userRepository.findByEmailAndIsActive(email, 1L);
    }
}
