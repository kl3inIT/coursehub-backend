package com.coursehub.converter;

import com.coursehub.dto.response.report.ReportResponseDTO;
import com.coursehub.entity.CommentEntity;
import com.coursehub.entity.ReportEntity;
import com.coursehub.entity.ReviewEntity;
import com.coursehub.enums.ResourceType;
import com.coursehub.repository.CommentRepository;
import com.coursehub.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
