package com.coursehub.service.impl;

import com.coursehub.repository.CertificateRepository;
import com.coursehub.service.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class CertificateServiceImpl implements CertificateService {


    private final CertificateRepository certificateRepository;

    @Override
    public Long countByUserEntityId(Long courseId) {
        return certificateRepository.countByUserEntity_Id(courseId);
    }
}
