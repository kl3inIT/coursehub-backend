package com.coursehub.converter;


import com.coursehub.dto.response.enrollment.EnrollmentResponseDTO;
import com.coursehub.entity.EnrollmentEntity;
import com.coursehub.exception.enrollment.EnrollNotFoundException;
import com.coursehub.service.EnrollmentService;
import com.coursehub.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentConverter {

    private final ModelMapper modelMapper;
    private final S3Service s3Service;

    public EnrollmentResponseDTO toResponseDTO(EnrollmentEntity enrollmentEntity){
        if (enrollmentEntity == null) {
            throw new EnrollNotFoundException("Enrollment not found");
        }
        EnrollmentResponseDTO enrollmentResponseDTO = modelMapper.map(enrollmentEntity, EnrollmentResponseDTO.class);
        return enrollmentResponseDTO;
    }

    public Page<EnrollmentResponseDTO> toResponseDTOPage(Page<EnrollmentEntity> enrollmentEntityPage){
        return enrollmentEntityPage.map(enrollmentEntity -> toResponseDTO(enrollmentEntity));
    }
}
