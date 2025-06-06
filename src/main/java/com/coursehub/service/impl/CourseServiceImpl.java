package com.coursehub.service.impl;

import com.coursehub.converter.CourseConverter;
import com.coursehub.dto.request.course.CourseCreationRequestDTO;
import com.coursehub.dto.response.course.CourseDetailsResponseDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.enums.CourseLevel;
import com.coursehub.exceptions.course.CourseCreationException;
import com.coursehub.exceptions.course.CourseNotFoundException;
import com.coursehub.exceptions.course.FileUploadException;
import com.coursehub.repository.CourseRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.*;
import com.coursehub.utils.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseConverter courseConverter;
    private final ModuleService moduleService;
    private final S3Service s3Service;
    private final LessonService lessonService;
    private final ReviewService reviewService;
    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CourseResponseDTO createCourse(Long managerId, CourseCreationRequestDTO courseRequestDTO) {
        log.info("Creating new course: {}", courseRequestDTO.getTitle());

        try {
            CourseEntity courseEntity = courseConverter.toEntity(courseRequestDTO);
            courseEntity.setUserEntity(userRepository.findById(managerId).
                    orElseThrow(() -> new CourseCreationException("Manager not found with ID: " + managerId)));
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
    public Page<CourseResponseDTO> searchCourses(String search, Long categoryId, CourseLevel level,
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
    public CourseDetailsResponseDTO findCourseDetailsById(Long courseId) {
        log.info("Finding course details for ID: {}", courseId);

        CourseEntity courseEntity = findCourseEntityById(courseId);
        CourseDetailsResponseDTO responseDTO = toDetailsResponseDTO(courseEntity);

        log.info("Successfully found course details for ID: {}", courseId);
        return responseDTO;
    }

    private CourseDetailsResponseDTO toDetailsResponseDTO(CourseEntity courseEntity) {
        if (courseEntity == null) {
            throw new CourseNotFoundException("Course not found");
        }

        return CourseDetailsResponseDTO.builder()
                .id(courseEntity.getId())
                .title(courseEntity.getTitle())
                .description(courseEntity.getDescription())
                .price(courseEntity.getPrice())
                .discount(courseEntity.getDiscount())
                .thumbnailUrl(s3Service.generatePermanentUrl(courseEntity.getThumbnail()))
                .category(courseEntity.getCategoryEntity().getName())
                .level(courseEntity.getLevel().getLevelName())
                .status(courseEntity.getStatus().getStatusName())
                .instructorName("CourseHub Team")
                .updatedAt(String.valueOf(courseEntity.getModifiedDate()))
                .finalPrice(courseConverter.calculateFinalPrice(courseEntity))
                .totalDuration(lessonService.calculateTotalDurationByCourseId(courseEntity.getId()))
                .totalLessons(lessonService.countLessonsByCourseId(courseEntity.getId()))
                .averageRating(reviewService.getAverageRating(courseEntity.getId()))
                .totalReviews(reviewService.getTotalReviews(courseEntity.getId()))
                .totalStudents(enrollmentService.countByCourseEntityId(courseEntity.getId()))
                .totalModules(moduleService.countByCourseEntityId(courseEntity.getId()))
                .modules(moduleService.getModulesByCourseId(courseEntity.getId()))
                .build();
    }


}