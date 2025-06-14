package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.module.ModuleRequestDTO;
import com.coursehub.dto.response.module.ModuleResponseDTO;
import com.coursehub.service.ModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.coursehub.constant.Constant.CommonConstants.*;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Slf4j
public class ModuleController {

    private final ModuleService moduleService;

    // implement create module api
    @PostMapping("/{courseId}")
    public ResponseEntity<ResponseGeneral<ModuleResponseDTO>> createModule(
            @PathVariable Long courseId,
            @Valid @RequestBody ModuleRequestDTO requestDTO) {

        log.info("Creating new module for course ID: {}", courseId);

        ModuleResponseDTO createdModule = moduleService.createModule(courseId, requestDTO);

        ResponseGeneral<ModuleResponseDTO> response = new ResponseGeneral<>();
        response.setData(createdModule);
        response.setMessage(SUCCESS);
        response.setDetail("Module created successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<String> deleteModule(@PathVariable Long moduleId) {
        log.info("Deleting module with id: {}", moduleId);
        moduleService.deleteModule(moduleId);
        return new ResponseEntity<>("Module deleted successfully", HttpStatus.OK);
    }


//    @GetMapping("/course/{courseId}")
//    public ResponseEntity<ResponseGeneral<Page<ModuleResponseDTO>>> getModulesByCourseId(
//            @PathVariable Long courseId,
//            Pageable pageable) {
//        log.info("Fetching modules for course ID: {}", courseId);
//        Page<ModuleResponseDTO> modules = moduleService.getModulesByCourseId(courseId);
//        ResponseGeneral<Page<ModuleResponseDTO>> response = new ResponseGeneral<>();
//        response.setData(modules);
//        response.setMessage(SUCCESS);
//        response.setDetail("Modules fetched successfully");
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/{moduleId}")
    public ResponseEntity<ResponseGeneral<ModuleResponseDTO>> getModuleById(@PathVariable Long moduleId) {
        log.info("Fetching module with ID: {}", moduleId);
        ModuleResponseDTO module = moduleService.getModuleById(moduleId);
        ResponseGeneral<ModuleResponseDTO> response = new ResponseGeneral<>();
        response.setData(module);
        response.setMessage(SUCCESS);
        response.setDetail("Module fetched successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{moduleId}")
    public ResponseEntity<ResponseGeneral<ModuleResponseDTO>> updateModule(
            @PathVariable Long moduleId,
            @Valid @RequestBody ModuleRequestDTO requestDTO) {
        log.info("Updating module with ID: {}", moduleId);
        ModuleResponseDTO updatedModule = moduleService.updateModule(moduleId, requestDTO);
        ResponseGeneral<ModuleResponseDTO> response = new ResponseGeneral<>();
        response.setData(updatedModule);
        response.setMessage(SUCCESS);
        response.setDetail("Module updated successfully");
        return ResponseEntity.ok(response);
    }
}
