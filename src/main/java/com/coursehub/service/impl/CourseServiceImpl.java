package com.coursehub.service.impl;

import com.coursehub.converter.CourseConverter;
import com.coursehub.dto.request.course.CourseCreationRequestDTO;
import com.coursehub.dto.request.course.CourseUpdateStatusAndLevelRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.ReviewEntity;
import com.coursehub.exception.course.CourseCreationException;
import com.coursehub.exception.course.CourseNotFoundException;
import com.coursehub.exception.course.FileUploadException;
import com.coursehub.repository.CourseRepository;
import com.coursehub.service.CourseService;
import com.coursehub.service.S3Service;
import com.coursehub.utils.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public CourseResponseDTO createCourse(CourseCreationRequestDTO courseRequestDTO) {
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
        CourseEntity courseEntity = findCourseEntityById(courseId);

        // Validate file using utility
        FileValidationUtil.validateImageFile(file);

        try {
            String objectKey = String.format("public/thumbnails/%d/%s", courseId, file.getOriginalFilename());
            // Upload to S3
            String thumbnailKey = s3Service.uploadFile(objectKey, file.getContentType(), file.getBytes());
            // Update course thumbnail in database
            courseEntity.setThumbnail(thumbnailKey);
            courseRepository.save(courseEntity);
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

        CourseEntity courseEntity = findCourseEntityById(courseId);

        log.info("Successfully found course: {} (ID: {})", courseEntity.getTitle(), courseId);
        return courseConverter.toResponseDTO(courseEntity);
    }

    @Override
    @Transactional
    public CourseResponseDTO updateCourseStatusAndLevel(Long courseId, CourseUpdateStatusAndLevelRequestDTO updateDTO) {
        log.info("Updating status and level for course ID: {} to status: {}, level: {}", 
            courseId, updateDTO.getStatus(), updateDTO.getLevel());

        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }

        if (updateDTO.getStatus() == null || updateDTO.getLevel() == null) {
            throw new IllegalArgumentException("Update request, status, and level cannot be null");
        }

        try {
            CourseEntity courseEntity = findCourseEntityById(courseId);

            String oldStatus = courseEntity.getStatus();
            String oldLevel = courseEntity.getLevel();
            String newStatus = updateDTO.getStatus();
            String newLevel = updateDTO.getLevel();
            
            courseEntity.setStatus(newStatus);
            courseEntity.setLevel(newLevel);
            courseRepository.save(courseEntity);

            log.info("Successfully updated course ID: {} status from {} to {} and level from {} to {}", 
                courseId, oldStatus, newStatus, oldLevel, newLevel);
            return courseConverter.toResponseDTO(courseEntity);
            
        } catch (CourseNotFoundException e) {
            log.error("Course not found with ID: {}", courseId);
            throw e;
        } catch (Exception e) {
            log.error("Failed to update course status and level for ID: {}: {}", courseId, e.getMessage(), e);
            throw new CourseCreationException("Failed to update course status and level: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<CourseResponseDTO> findAllCourse(Pageable pageable) {

        Page<CourseEntity> courseEntities = courseRepository.findAll(pageable);
        if (courseEntities.isEmpty())
            log.warn("No courses found");

        return courseConverter.toResponseDTOPage(courseEntities);
    }

    @Override
    public List<CourseResponseDTO> findByCategoryId(Long categoryId) {

        List<CourseEntity> courses = courseRepository.findByCategoryEntity_Id(categoryId);
        if (courses.isEmpty())
            log.warn("Course is empty");

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

    @Override
    public CourseEntity findCourseEntityById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.warn("Course not found with ID: {}", courseId);
                    return new CourseNotFoundException(courseId);
                });
    }

    @Override
    public Double getAverageRating(Long courseId) {
        CourseEntity course = findCourseEntityById(courseId);

        if (course.getReviewEntities() == null || course.getReviewEntities().isEmpty()) {
            return 0.0;
        }

        return course.getReviewEntities().stream()
                .mapToInt(ReviewEntity::getStar)
                .average()
                .orElse(0.0);
    }

    @Override
    public Long getTotalReviews(Long courseId) {
        CourseEntity course = findCourseEntityById(courseId);

        return (long) (course.getReviewEntities() != null ? 
                course.getReviewEntities().size() : 0);
    }
}