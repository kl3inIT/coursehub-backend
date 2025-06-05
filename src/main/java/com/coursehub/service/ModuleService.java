package com.coursehub.service;

import com.coursehub.dto.request.module.ModuleRequestDTO;
import com.coursehub.dto.response.module.ModuleResponseDTO;
import com.coursehub.entity.ModuleEntity;

import java.util.List;


public interface ModuleService {

    ModuleResponseDTO createModule(Long courseId, ModuleRequestDTO requestDTO);

    List<ModuleResponseDTO> getModulesByCourseId(Long courseId);

    ModuleResponseDTO getModuleById(Long moduleId);

    ModuleResponseDTO updateModule(Long moduleId, ModuleRequestDTO requestDTO);

    void deleteModule(Long moduleId);
//    void reorderModules(Long courseId, List<Long> orderedModuleIds);
    ModuleEntity findModuleEntityById(Long moduleId);
}
