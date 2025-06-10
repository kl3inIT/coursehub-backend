package com.coursehub.service.impl;

import com.coursehub.dto.request.module.ModuleRequestDTO;
import com.coursehub.dto.response.module.ModuleResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.ModuleEntity;
import com.coursehub.exceptions.course.CourseNotFoundException;
import com.coursehub.exceptions.module.ModuleNotFoundException;
import com.coursehub.repository.CourseRepository;
import com.coursehub.repository.ModuleRepository;
import com.coursehub.service.CourseService;
import com.coursehub.service.LessonService;
import com.coursehub.service.ModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final LessonService lessonService;
    private final CourseRepository courseRepository;

    @Override
    public ModuleResponseDTO createModule(Long courseId, ModuleRequestDTO requestDTO) {
        CourseEntity courseEntity = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
        Long maxOrderNumber = moduleRepository.findMaxOrderNumberByCourseId(courseId);
        Long orderNumber = (maxOrderNumber == null) ? 1L : maxOrderNumber + 1;
        ModuleEntity module = ModuleEntity.builder()
                .title(requestDTO.getTitle())
                .courseEntity(courseEntity)
                .orderNumber(orderNumber)
                .build();
        ModuleEntity savedModule = moduleRepository.save(module);
        log.info("Module created successfully with ID: {}", savedModule.getId());
        return ModuleResponseDTO.builder()
                .id(savedModule.getId())
                .title(savedModule.getTitle())
                .courseId(savedModule.getCourseEntity().getId())
                .orderNumber(savedModule.getOrderNumber())
                .build();
    }


    @Override
    public void deleteModule(Long moduleId) {
        ModuleEntity module = findModuleEntityById(moduleId);
        moduleRepository.delete(module);
        log.info("Module with ID {} deleted successfully", moduleId);
    }

    @Override
    public ModuleEntity findModuleEntityById(Long moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException("Module not found with id: " + moduleId));
    }

    @Override
    public List<ModuleResponseDTO> getModulesByCourseId(Long courseId) {
        List<ModuleEntity> modules = moduleRepository.findByCourseEntityId(courseId);
        return modules.stream()
                .map(module -> ModuleResponseDTO.builder()
                        .id(module.getId())
                        .title(module.getTitle())
                        .courseId(module.getCourseEntity().getId())
                        .orderNumber(module.getOrderNumber())
                        .totalLessons(lessonService.countLessonsByModuleId(module.getId()))
                        .totalDuration(lessonService.calculateTotalDurationByModuleId(module.getId()))
                        .build())
                .toList();
    }

    @Override
    public ModuleResponseDTO getModuleById(Long moduleId) {
        ModuleEntity module = findModuleEntityById(moduleId);
        return ModuleResponseDTO.builder()
                .id(module.getId())
                .title(module.getTitle())
                .courseId(module.getCourseEntity().getId())
                .orderNumber(module.getOrderNumber())
                .build();
    }

    @Override
    public ModuleResponseDTO updateModule(Long moduleId, ModuleRequestDTO requestDTO) {
        ModuleEntity module = findModuleEntityById(moduleId);
        module.setTitle(requestDTO.getTitle());
        ModuleEntity updatedModule = moduleRepository.save(module);
        log.info("Module with ID {} updated successfully", moduleId);
        return ModuleResponseDTO.builder()
                .id(updatedModule.getId())
                .title(updatedModule.getTitle())
                .courseId(updatedModule.getCourseEntity().getId())
                .orderNumber(updatedModule.getOrderNumber())
                .build();
    }

    @Override
    public Long countByCourseEntityId(Long courseId) {
        return moduleRepository.countModuleEntitiesByCourseEntityId(courseId);
    }

}