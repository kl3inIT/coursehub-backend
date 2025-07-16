package com.coursehub.converter;

import com.coursehub.dto.response.report.AggregatedReportDTO;
import com.coursehub.dto.response.report.ReportDetailDTO;
import com.coursehub.dto.response.report.ReportResponseDTO;
import com.coursehub.entity.CommentEntity;
import com.coursehub.entity.ReportEntity;
import com.coursehub.entity.ReviewEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.ResourceType;
import com.coursehub.repository.CommentRepository;
import com.coursehub.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReportConverter {

    private final CommentRepository commentRepository;

    private final ReviewRepository reviewRepository;

    // Entity -> ResponseDTO
     public ReportResponseDTO toResponseDTO(ReportEntity reportEntity) {
         if (reportEntity == null) {
             return null;
         }
         ReportResponseDTO responseDTO = new ReportResponseDTO();
         if(reportEntity.getType().equals(ResourceType.COMMENT)) {
             Optional<CommentEntity> commentEntity = commentRepository.findById(reportEntity.getResourceId());
             commentEntity.ifPresent(entity -> responseDTO.setDescription(entity.getComment()));
             commentEntity.ifPresent(entity -> responseDTO.setHidden(entity.getIsHidden() == 1));
         }else if(reportEntity.getType().equals(ResourceType.REVIEW)) {
             Optional<ReviewEntity> reviewEntity = reviewRepository.findById(reportEntity.getResourceId());
             reviewEntity.ifPresent(entity -> responseDTO.setDescription(entity.getComment()));
             reviewEntity.ifPresent(entity -> responseDTO.setHidden(entity.getIsHidden() == 1));
         }

         responseDTO.setReportId(reportEntity.getId());
         responseDTO.setType(String.valueOf(reportEntity.getType()));
         responseDTO.setResourceId(reportEntity.getResourceId());
         responseDTO.setReporterName(reportEntity.getReporter().getName());
         responseDTO.setReportedUserName(reportEntity.getReportedUser().getName());
         responseDTO.setReportedUserId(reportEntity.getReportedUser().getId());
         responseDTO.setReason(reportEntity.getReason());
         responseDTO.setSeverity(String.valueOf(reportEntity.getSeverity()));
         responseDTO.setStatus(String.valueOf(reportEntity.getStatus()));
         responseDTO.setWarningCount(reportEntity.getReportedUser().getWarningCount());
         responseDTO.setReportedUserStatus(String.valueOf(reportEntity.getReportedUser().getIsActive()));
         responseDTO.setReportedUserMemberSince(reportEntity.getCreatedDate().toString());
         responseDTO.setCreatedAt(reportEntity.getCreatedDate());
         return responseDTO;
     }

    private void mapResourceOwnerInfo(AggregatedReportDTO dto, String content, UserEntity user, boolean isHidden) {
        dto.setResourceContent(content);
        dto.setResourceOwner(user.getName());
        dto.setResourceOwnerId(user.getId());
        dto.setResourceOwnerAvatar(user.getAvatar());
        dto.setResourceOwnerStatus(user.getIsActive().name());
        dto.setResourceOwnerMemberSince(user.getCreatedDate().toString());
        dto.setHidden(isHidden);
    }


    public void mapResourceInfo(AggregatedReportDTO dto, ReportEntity report, long totalReports) {
        dto.setResourceId(report.getResourceId());
        dto.setResourceType(report.getType().name());
        dto.setSeverity(report.getSeverity());
        dto.setStatus(report.getStatus());
        dto.setCreatedAt(report.getCreatedDate());
        dto.setTotalReports(totalReports);

        if (report.getType() == ResourceType.COMMENT) {
            commentRepository.findById(report.getResourceId()).ifPresent(
                    comment -> mapResourceOwnerInfo(dto, comment.getComment(), comment.getUserEntity(), comment.getIsHidden() == 1));
        } else if (report.getType() == ResourceType.REVIEW) {
            reviewRepository.findById(report.getResourceId()).ifPresent(
                    review -> mapResourceOwnerInfo(dto, review.getComment(), review.getUserEntity(), review.getIsHidden() == 1));
        }

    }

    public List<ReportDetailDTO> toReportDetailList(List<ReportEntity> reports) {
        return reports.stream().map(r -> {
            ReportDetailDTO d = new ReportDetailDTO();
            d.setReportId(r.getId());
            d.setReporterName(r.getReporter().getName());
            d.setReason(r.getReason());
            d.setCreatedAt(r.getCreatedDate());
            d.setReporterId(r.getReporter().getId());
            d.setReporterAvatar(r.getReporter().getAvatar());
            d.setSeverity(r.getSeverity());
            return d;
        }).toList();
    }
}
