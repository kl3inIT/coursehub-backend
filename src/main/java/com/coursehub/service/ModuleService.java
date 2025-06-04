package com.coursehub.service;

import com.coursehub.dto.request.module.ModuleRequestDTO;
import com.coursehub.dto.response.module.ModuleResponseDTO;
import com.coursehub.entity.ModuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ModuleService {

    ModuleResponseDTO createModule(Long courseId, ModuleRequestDTO requestDTO);

    Page<ModuleResponseDTO> getModulesByCourseId(Long courseId, Pageable pageable);

    ModuleResponseDTO getModuleById(Long moduleId);

    ModuleResponseDTO updateModule(Long moduleId, ModuleRequestDTO requestDTO);

    void deleteModule(Long moduleId);
//    void reorderModules(Long courseId, List<Long> orderedModuleIds);
    ModuleEntity findModuleEntityById(Long moduleId);
}
