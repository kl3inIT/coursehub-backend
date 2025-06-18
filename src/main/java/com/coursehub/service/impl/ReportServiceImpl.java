package com.coursehub.service.impl;

import com.coursehub.converter.ReportConverter;
import com.coursehub.dto.request.report.ReportRequestDTO;
import com.coursehub.dto.request.report.ReportSearchRequestDTO;
import com.coursehub.dto.request.report.ReportStatusDTO;
import com.coursehub.dto.response.report.ReportResponseDTO;
import com.coursehub.dto.response.report.ResourceLocationDTO;
import com.coursehub.entity.*;
import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;
import com.coursehub.exceptions.comment.CommentNotFoundException;
import com.coursehub.exceptions.report.*;
import com.coursehub.exceptions.review.ReviewNotFoundException;
import com.coursehub.repository.*;
import com.coursehub.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportConverter reportConverter;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public ReportResponseDTO createReport(ReportRequestDTO dto) {
        UserEntity reporter = getCurrentUser();
        if (dto.getReason().length() > 500) {
            throw new ReasonTooLongException("Reason must be less than 500 characters.");
        }
        boolean alreadyReported = reportRepository.existsByReporterAndResourceIdAndType(reporter, dto.getResourceId(), dto.getResourceType());
        if (alreadyReported) {
            throw new ContentAlreadyReportedException("Content already reported.");
        }
        if (!isAllowedToReport(reporter.getId())) {
            throw new TooManyRequestsException("You have reported too many times in the past hour. Please try again later.");
        }
        ReportEntity report = new ReportEntity();
        if(dto.getResourceType().equals(ResourceType.COMMENT)) {
            CommentEntity comment = commentRepository.findByIdWithUser(dto.getResourceId()) //Fetch comment để lấy thông tin userEntity
                    .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + dto.getResourceId()));
            if(comment.getIsHidden() == 1){
                throw new ContentAlreadyHiddenException("Cannot report a hidden comment.");
            }
            UserEntity reportedUser = comment.getUserEntity();
            report.setReportedUser(reportedUser);
        } else {
            ReviewEntity review = reviewRepository.findByIdWithUser(dto.getResourceId())
                    .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + dto.getResourceId()));
            if(review.getIsHidden() == 1){
                throw new ContentAlreadyHiddenException("Cannot report a hidden review.");
            }
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
        incrementReportCount(reporter.getId());
        return reportConverter.toResponseDTO(report);
    }

    @Override
    public Page<ReportResponseDTO> searchReports(ReportSearchRequestDTO request) {
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDir()), request.getSortBy());
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<ReportEntity> reports = reportRepository.findAllWithFilters(
                request.getType(),
                request.getSeverity(),
                request.getStatus(),
                request.getSearch(),
                pageable
        );
        return reports.map(reportConverter::toResponseDTO);
    }


    public ReportEntity findReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found with ID: " + reportId));
    }

    @Override
    public ReportResponseDTO getReportById(Long reportId) {
        ReportEntity report = findReportById(reportId);
        return reportConverter.toResponseDTO(report);
    }

    @Override
    @Transactional
    public ReportResponseDTO updateReportStatus(Long reportId, ReportStatusDTO statusDTO) {
        ReportEntity report = findReportById(reportId);

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

    @Override
    public ResourceLocationDTO getResourceLocationByReportId(Long reportId) {
        ReportEntity report = findReportById(reportId);
        return getResourceLocation(report.getType(), report.getResourceId());
    }

    @Override
    public ResourceLocationDTO getResourceLocation(ResourceType type, Long resourceId) {
        ResourceLocationDTO location = new ResourceLocationDTO();
        location.setResourceType(type.name());
        location.setResourceId(resourceId);

        if (type == ResourceType.COMMENT) {
            CommentEntity comment = commentRepository.findById(resourceId)
                    .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + resourceId));
            LessonEntity lesson = comment.getLessonEntity();
            ModuleEntity module = lesson.getModuleEntity();
            CourseEntity course = module.getCourseEntity();

            location.setLessonId(lesson.getId());
            location.setLessonName(lesson.getTitle());

            location.setModuleId(module.getId());
            location.setModuleName(module.getTitle());

            location.setCourseId(course.getId());
            location.setCourseName(course.getTitle());
        } else if (type == ResourceType.REVIEW) {
            ReviewEntity review = reviewRepository.findById(resourceId)
                    .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + resourceId));
            location.setCourseId(review.getCourseEntity().getId());
            location.setCourseName(review.getCourseEntity().getTitle());
        }
        return location;
    }

    public boolean isAllowedToReport(Long userId) {
        String key = "report:count:user:" + userId;
        int limit = 5;
        String val = redisTemplate.opsForValue().get(key);
        long current = 0;
        if (val != null) {
            try {
                current = Long.parseLong(val);
            } catch (NumberFormatException e) {
                current = 0;
            }
        }
        return current < limit;
    }

    public void incrementReportCount(Long userId) {
        String key = "report:count:user:" + userId;
        long expireTime = 60L * 60L;
        Long current = redisTemplate.opsForValue().increment(key);
        if (current != null && current == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(expireTime));
        }
    }


}
