package com.coursehub.converter;


import com.coursehub.dto.response.enrollment.EnrollmentResponseDTO;
import com.coursehub.entity.EnrollmentEntity;
import com.coursehub.exceptions.enrollment.EnrollNotFoundException;
import com.coursehub.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentConverter {

    private final ModelMapper modelMapper;

    public EnrollmentResponseDTO toResponseDTO(EnrollmentEntity enrollmentEntity){
        if (enrollmentEntity == null) {
            throw new EnrollNotFoundException("Enrollment not found");
        }
        return modelMapper.map(enrollmentEntity, EnrollmentResponseDTO.class);

    }

    public Page<EnrollmentResponseDTO> toResponseDTOPage(Page<EnrollmentEntity> enrollmentEntityPage){
        return enrollmentEntityPage.map(this::toResponseDTO);
    }
}
