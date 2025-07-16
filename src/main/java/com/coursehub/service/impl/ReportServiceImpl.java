package com.coursehub.service.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coursehub.converter.ReportConverter;
import com.coursehub.dto.request.report.ReportRequestDTO;
import com.coursehub.dto.request.report.ReportSearchRequestDTO;
import com.coursehub.dto.request.report.ReportStatusDTO;
import com.coursehub.dto.response.report.AggregatedReportDTO;
import com.coursehub.dto.response.report.ReportResponseDTO;
import com.coursehub.dto.response.report.ResourceLocationDTO;
import com.coursehub.entity.CommentEntity;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.LessonEntity;
import com.coursehub.entity.ModuleEntity;
import com.coursehub.entity.ReportEntity;
import com.coursehub.entity.ReviewEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.ReportSeverity;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;
import com.coursehub.enums.UserStatus;
import com.coursehub.exceptions.comment.CommentNotFoundException;
import com.coursehub.exceptions.report.ContentAlreadyHiddenException;
import com.coursehub.exceptions.report.ContentAlreadyReportedException;
import com.coursehub.exceptions.report.ReasonTooLongException;
import com.coursehub.exceptions.report.ReportNotFoundException;
import com.coursehub.exceptions.report.TooManyRequestsException;
import com.coursehub.exceptions.review.ReviewNotFoundException;
import com.coursehub.repository.CommentRepository;
import com.coursehub.repository.ReportRepository;
import com.coursehub.repository.ReviewRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.NotificationService;
import com.coursehub.service.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportConverter reportConverter;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final StringRedisTemplate redisTemplate;
    private final NotificationService notificationService;

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
    public void updateAllReportsStatusByResourceId(Long resourceId, ReportStatusDTO statusDTO) {
        List<ReportEntity> reports = reportRepository.findByResourceId(resourceId);
        
        if (reports.isEmpty()) {
            throw new ReportNotFoundException("No reports found for resource ID: " + resourceId);
        }

        UserEntity currentUser = getCurrentUser();
        Date now = new Date();

        for (ReportEntity report : reports) {
            if (report.getStatus().equals(ReportStatus.PENDING)) {
                report.setStatus(statusDTO.getStatus());
                report.setActionNote(statusDTO.getActionNote());
                report.setResolvedBy(currentUser);
                report.setModifiedDate(now);
                
                // Gửi notification cho reporter
                notificationService.notifyReportStatusUpdate(
                    report.getReporter().getId(),
                    report.getType(),
                    resourceId,
                    statusDTO.getStatus(),
                    statusDTO.getActionNote()
                );
            }
        }
        
        reportRepository.saveAll(reports);
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
        return userRepository.findByEmailAndIsActive(email, UserStatus.ACTIVE);
    }

    @Override
    public ResourceLocationDTO getResourceLocationByReportId(Long reportId) {
        ReportEntity report = findReportById(reportId);
        return getResourceLocation(report.getType(), report.getResourceId());
    }

    @Override
    public ResourceLocationDTO getResourceLocationByResourceId(Long resourceId) {
        // Tìm report đầu tiên có resourceId này để xác định type
        List<ReportEntity> reports = reportRepository.findByResourceId(resourceId);
        if (reports.isEmpty()) {
            throw new ReportNotFoundException("No reports found for resource ID: " + resourceId);
        }
        
        ReportEntity firstReport = reports.getFirst();
        return getResourceLocation(firstReport.getType(), resourceId);
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

    @Override
    public Page<AggregatedReportDTO> getAggregatedReports(ReportSearchRequestDTO searchRequest) {
        List<ReportEntity> allReports = reportRepository.findAll();
        List<ReportEntity> filteredReports = applyFilters(allReports, searchRequest);

        Map<String, List<ReportEntity>> grouped = filteredReports.stream()
                .collect(Collectors.groupingBy(r -> r.getType() + "-" + r.getResourceId()));

        List<AggregatedReportDTO> result = new ArrayList<>(grouped.values().stream()
                .map(this::mapToAggregatedReport)
                .toList());

        Comparator<AggregatedReportDTO> comparator = getReportComparator(searchRequest.getSortBy());
        if ("desc".equalsIgnoreCase(searchRequest.getSortDir())) {
            comparator = comparator.reversed();
        }
        result.sort(comparator);

        // Paging
        int page = searchRequest.getPage() != null ? searchRequest.getPage() : 0;
        int size = searchRequest.getSize() != null ? searchRequest.getSize() : 10;
        int start = page * size;
        int end = Math.min(start + size, result.size());
        List<AggregatedReportDTO> pageContent = (start < end) ? result.subList(start, end) : new ArrayList<>();
        return new PageImpl<>(pageContent, PageRequest.of(page, size), result.size());
    }


    private List<ReportEntity> applyFilters(List<ReportEntity> reports, ReportSearchRequestDTO searchRequest) {
        List<ReportEntity> filtered = reports;
        if (searchRequest.getType() != null) {
            filtered = filtered.stream().filter(r -> r.getType() == searchRequest.getType()).toList();
        }
        if (searchRequest.getStatus() != null) {
            filtered = filtered.stream().filter(r -> r.getStatus() == searchRequest.getStatus()).toList();
        }
        if (searchRequest.getSeverity() != null) {
            filtered = filtered.stream().filter(r -> r.getSeverity() == searchRequest.getSeverity()).toList();
        }
        if (searchRequest.getSearch() != null && !searchRequest.getSearch().trim().isEmpty()) {
            String searchTerm = searchRequest.getSearch().toLowerCase().trim();
            filtered = filtered.stream()
                    .filter(r -> {
                        if (r.getType() == ResourceType.COMMENT) {
                            CommentEntity comment = commentRepository.findById(r.getResourceId()).orElse(null);
                            return comment != null && comment.getComment().toLowerCase().contains(searchTerm);
                        } else if (r.getType() == ResourceType.REVIEW) {
                            ReviewEntity review = reviewRepository.findById(r.getResourceId()).orElse(null);
                            return review != null && review.getComment().toLowerCase().contains(searchTerm);
                        }
                        return false;
                    }).toList();
        }
        return filtered;
    }

    private Comparator<AggregatedReportDTO> getReportComparator(String sortBy) {
        return switch (sortBy) {
            case "severity" -> Comparator.comparing(AggregatedReportDTO::getSeverity, Comparator.nullsLast(Enum::compareTo));
            case "status" -> Comparator.comparing(AggregatedReportDTO::getStatus, Comparator.nullsLast(Enum::compareTo));
            case "totalReports" -> Comparator.comparing(AggregatedReportDTO::getTotalReports, Comparator.nullsLast(Long::compareTo));
            case "resourceContent" -> Comparator.comparing(AggregatedReportDTO::getResourceContent, Comparator.nullsLast(String::compareToIgnoreCase));
            case "resourceOwner" -> Comparator.comparing(AggregatedReportDTO::getResourceOwner, Comparator.nullsLast(String::compareToIgnoreCase));
            case "hidden" -> Comparator.comparing(AggregatedReportDTO::isHidden);
            default -> Comparator.comparing(AggregatedReportDTO::getCreatedAt, Comparator.nullsLast(Date::compareTo));
        };
    }

    private AggregatedReportDTO mapToAggregatedReport(List<ReportEntity> group) {
        ReportEntity first = group.getFirst();
        AggregatedReportDTO dto = new AggregatedReportDTO();
        reportConverter.mapResourceInfo(dto, first, group.size());
        dto.setReports(reportConverter.toReportDetailList(group));
        return dto;
    }

    @Override
    public AggregatedReportDTO getAggregatedReportByResourceId(Long resourceId) {
        // Tìm tất cả reports cho resourceId này
        List<ReportEntity> reports = reportRepository.findByResourceId(resourceId);

        if (reports.isEmpty()) {
            throw new ReportNotFoundException("No reports found for resource ID: " + resourceId);
        }

        ReportEntity first = reports.getFirst();
        AggregatedReportDTO dto = new AggregatedReportDTO();
        reportConverter.mapResourceInfo(dto, first, reports.size());

        // Map các report con
        dto.setReports(reportConverter.toReportDetailList(reports));
        return dto;
    }

}
