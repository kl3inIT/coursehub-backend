package com.coursehub.service.impl;

import com.coursehub.converter.CourseConverter;
import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CategoryEntity;
import com.coursehub.entity.CourseEntity;
import com.coursehub.exception.course.*;
import com.coursehub.repository.CourseRepository;
import com.coursehub.service.CourseService;
import com.coursehub.service.S3Service;
import com.coursehub.utils.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseConverter courseConverter;
    private final S3Service s3Service;

    @Override
    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO) {
        log.info("Creating new course: {}", courseRequestDTO.getTitle());

        try {
            CourseEntity courseEntity = courseConverter.toEntity(courseRequestDTO);
            courseRepository.save(courseEntity);
            log.info("Successfully created course with ID: {}", courseEntity.getId());

            return courseConverter.toResponseDTO(courseEntity);

        } catch (Exception e) {
            log.error("Failed to create course: {}", e.getMessage(), e);
            throw new CourseCreationException("Failed to create course: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadThumbnail(Long courseId, MultipartFile file) {
        log.info("Uploading thumbnail for course ID: {}", courseId);

        // Validate course exists
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        // Validate file using utility
        FileValidationUtil.validateImageFile(file);


        try {
            String objectKey = String.format("public/thumbnails/%d/%s", courseId, file.getOriginalFilename());

            // Upload to S3
            String thumbnailKey = s3Service.uploadFile(objectKey, file.getContentType(), file.getBytes());

            // Update course thumbnail in database
            course.setThumbnail(thumbnailKey);

            courseRepository.save(course);

            log.info("Successfully uploaded thumbnail for course ID: {}", courseId);
            return thumbnailKey;

        } catch (Exception e) {
            log.error("Failed to upload thumbnail for course ID: {}: {}", courseId, e.getMessage(), e);
            throw new FileUploadException("Failed to upload thumbnail: " + e.getMessage(), e);
        }
    }

    @Override
    public CourseResponseDTO findCourseById(Long courseId) {
        log.info("Finding course with ID: {}", courseId);

        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.warn("Course not found with ID: {}", courseId);
                    return new CourseNotFoundException(courseId);
                });

        log.info("Successfully found course: {} (ID: {})", course.getTitle(), courseId);
        return courseConverter.toResponseDTO(course);
    }

    @Override
    public Page<CourseResponseDTO> findAll(Pageable pageable) {

        Page<CourseEntity> courseEntities = courseRepository.findAll(pageable);
        if (courseEntities.isEmpty()) {
            log.warn("Course is empty");
        } else {
            log.info("Successfully found courses: {}", courseEntities);
        }
        return courseConverter.toResponseDTOPage(courseEntities);
    }

    @Override
    public List<CourseResponseDTO> findByCategoryId(Long categoryId) {

        List<CourseEntity> courses = courseRepository.findByCategoryEntity_Id(categoryId);
        if (courses.isEmpty()) {
            log.warn("Course is empty");
        } else {
            log.info("Successfully found courses: {}", courses);
        }
        return courseConverter.toResponseDTOList(courses);
    }

    @Override
    public List<CourseResponseDTO> findFeaturedCourses(Pageable pageable) {
        log.info("Finding featured courses with pageable: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        List<CourseEntity> featuredCourses = courseRepository.findFeaturedCourse(pageable);
        if (featuredCourses.isEmpty()) {
            log.warn("No featured courses found");
        } else {
            log.info("Found {} featured courses", featuredCourses.size());
        }
        return courseConverter.toResponseDTOList(featuredCourses);
    }

    @Override
    public Page<CourseResponseDTO> searchCourses(String search, Long categoryId, String level, 
                                               Double minPrice, Double maxPrice, Pageable pageable) {
        log.info("Searching courses with filters - search: {}, categoryId: {}, level: {}, minPrice: {}, maxPrice: {}", 
            search, categoryId, level, minPrice, maxPrice);

        Page<CourseEntity> courseEntities = courseRepository.searchCourses(
            search, categoryId, level, minPrice, maxPrice, pageable);
        
        if (courseEntities.isEmpty()) {
            log.warn("No courses found with applied filters");
        } else {
            log.info("Found {} courses with applied filters", courseEntities.getTotalElements());
        }
        
        return courseConverter.toResponseDTOPage(courseEntities);
    }

}