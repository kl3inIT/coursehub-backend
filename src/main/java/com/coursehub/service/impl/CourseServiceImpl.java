package com.coursehub.service.impl;

import com.coursehub.converter.CourseConverter;
import com.coursehub.dto.request.course.CourseCreationRequestDTO;
import com.coursehub.dto.request.course.CourseSearchRequestDTO;
import com.coursehub.dto.request.course.CourseUpdateRequestDTO;
import com.coursehub.dto.response.course.*;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.EnrollmentEntity;
import com.coursehub.entity.LessonEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.CourseStatus;
import com.coursehub.exceptions.course.*;
import com.coursehub.exceptions.user.UserNotFoundException;
import com.coursehub.repository.CourseRepository;
import com.coursehub.repository.SearchRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.*;
import com.coursehub.utils.FileValidationUtil;
import com.coursehub.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import static com.coursehub.constant.Constant.SearchConstants.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final SearchRepository searchRepository;

    @Override
    @Transactional
    public CourseCreateUpdateResponseDTO createCourse(CourseCreationRequestDTO courseRequestDTO) {
        log.info("Creating new course: {}", courseRequestDTO.getTitle());

        try {
            CourseEntity courseEntity = courseConverter.toEntity(courseRequestDTO);

            SecurityContext context = SecurityContextHolder.getContext();
            String email = context.getAuthentication().getName();
            UserEntity user = userRepository.findByEmailAndIsActive(email, 1L);
            if(user == null){
                throw new UserNotFoundException("User not found with email: " + email);
            }
            courseEntity.setUserEntity(user);
            courseRepository.save(courseEntity);
            log.info("Successfully created course with ID: {}", courseEntity.getId());

            return courseConverter.toCreateUpdateResponseDTO(courseEntity);

        } catch (Exception e) {
            log.error("Failed to create course: {}", e.getMessage(), e);
            throw new CourseCreationException("Failed to create course: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public CourseCreateUpdateResponseDTO updateCourse(Long courseId, CourseUpdateRequestDTO courseRequestDTO) {
        log.info("Updating course with ID: {}", courseId);

        // Validate course exists
        CourseEntity course = findCourseEntityById(courseId);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!course.getUserEntity().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("You are not allowed to update this course");
        }

        // Update course details
        courseConverter.updateEntityFromRequest(course, courseRequestDTO);

        try {
            // Save updated course
            CourseEntity updatedCourse = courseRepository.save(course);
            log.info("Successfully updated course with ID: {}", updatedCourse.getId());
            return courseConverter.toCreateUpdateResponseDTO(updatedCourse);

        } catch (Exception e) {
            log.error("Failed to update course with ID {}: {}", courseId, e.getMessage(), e);
            throw new CourseUpdateException("Failed to update course: " + e.getMessage());
        }
    }

    public String archiveCourse(Long courseId, String userEmail) {
        CourseEntity course = findCourseEntityById(courseId);
        UserEntity user = getActiveUserByEmail(userEmail);

        if (!canArchiveCourse(user, course)) {
            throw new UnauthorizedAccessException("You are not allowed to archive this course");
        }

        if (course.getStatus() == CourseStatus.ARCHIVED) {
            throw new CourseAlreadyArchivedException("Course is already archived.");
        }

        course.setStatus(CourseStatus.ARCHIVED);
        courseRepository.save(course);

        return "Course archived successfully.";
    }

    public String publishCourse(Long courseId, String userEmail) {
        CourseEntity course = findCourseEntityById(courseId);
        UserEntity user = getActiveUserByEmail(userEmail);

        if (!canPublishCourse(user, course)) {
            throw new UnauthorizedAccessException("You are not allowed to publish this course");
        }

        if (course.getModuleEntities() == null || course.getModuleEntities().isEmpty()) {
            throw new CourseInvalidStateException("Course must have at least one module before publishing.");
        }

        course.setStatus(CourseStatus.PUBLISHED);
        courseRepository.save(course);

        return "Course published successfully.";
    }

    public String restoreCourse(Long courseId, String userEmail) {
        CourseEntity course = findCourseEntityById(courseId);
        UserEntity user = getActiveUserByEmail(userEmail);

        if (!canRestoreCourse(user, course)) {
            throw new UnauthorizedAccessException("You are not allowed to restore this course");
        }

        if (course.getStatus() != CourseStatus.ARCHIVED) {
            throw new InvalidCourseRestoreStateException("Only archived courses can be restored.");
        }

        course.setStatus(CourseStatus.DRAFT); // hoặc PUBLISHED nếu muốn khôi phục thẳng
        courseRepository.save(course);

        return "Course restored successfully.";
    }

    private UserEntity getActiveUserByEmail(String email) {
        UserEntity user = userRepository.findByEmailAndIsActive(email, 1L);
        if (user == null) {
            throw new UserNotFoundException("User not found or inactive: " + email);
        }
        return user;
    }

    private boolean canArchiveCourse(UserEntity user, CourseEntity course) {
        return UserUtils.isAdmin(user) || UserUtils.isOwner(user, course);
    }

    private boolean canPublishCourse(UserEntity user, CourseEntity course) {
        return UserUtils.isAdmin(user) || UserUtils.isOwner(user, course);
    }

    private boolean canRestoreCourse(UserEntity user, CourseEntity course) {
        return UserUtils.isAdmin(user) || UserUtils.isOwner(user, course);
    }

    private Boolean canEditCourse(CourseEntity course) {
        SecurityContext context = SecurityContextHolder.getContext();

        String email = context.getAuthentication().getName();

        UserEntity userEntity = userRepository.findByEmailAndIsActive(email, 1L);
        if (userEntity == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        if (userEntity.getRoleEntity().getCode().equals("ADMIN")) {
            return true;
        }

        return course.getUserEntity().getId().equals(userEntity.getId());
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
    public List<ManagerCourseResponseDTO> findAllCourseByStatus(CourseStatus status) {
        List<CourseEntity> courseEntities = courseRepository.findAllByStatus(status);
        if (courseEntities.isEmpty())
            log.warn("No courses found");
        return courseEntities.stream()
                .map(this::toManagerCourseResponseDTO)
                .toList();
    }

    private ManagerCourseResponseDTO toManagerCourseResponseDTO(CourseEntity courseEntity) {
        if (courseEntity == null) {
            throw new CourseNotFoundException("Course not found");
        }

        return ManagerCourseResponseDTO.builder()
                .id(courseEntity.getId())
                .title(courseEntity.getTitle())
                .description(courseEntity.getDescription())
                .thumbnailUrl(s3Service.generatePermanentUrl(courseEntity.getThumbnail()))
                .category(courseEntity.getCategoryEntity().getName())
                .lastUpdatedDate(courseEntity.getModifiedDate())
                .rating(reviewService.getAverageRating(courseEntity.getId()))
                .totalEnrollments(enrollmentService.countByCourseEntityId(courseEntity.getId()))
                .canEdit(canEditCourse(courseEntity))
                .status(courseEntity.getStatus().getStatusName())
                .build();
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
        List<CourseEntity> featuredCourses = courseRepository.findFeaturedCourse(CourseStatus.PUBLISHED ,pageable);
        if (featuredCourses.isEmpty()) {
            log.warn("No featured courses found");
        } else {
            log.info("Found {} featured courses", featuredCourses.size());
        }
        return courseConverter.toResponseDTOList(featuredCourses);
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

    @Override
    public CourseEntity findCourseEntityByLessonId(Long lessonId) {
        LessonEntity lessonEntity = lessonService.getLessonEntityById(lessonId);
        return lessonEntity.getModuleEntity().getCourseEntity();
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

    @Override
    public List<DashboardCourseResponseDTO> getCoursesByUserId() {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity user = userRepository.findByEmailAndIsActive(email, 1L);
        if(user == null){
            throw new UserNotFoundException("User not found with email: " + email);
        }
        List<EnrollmentEntity> enrollment = enrollmentService.getEnrollmentsByUserEntityId(user.getId());

        return enrollment.stream()
                .map(e -> toDashboardCourseResponseDTO(e.getCourseEntity(), e))
                .toList();
    }

    private DashboardCourseResponseDTO toDashboardCourseResponseDTO(CourseEntity courseEntity,
            EnrollmentEntity enrollmentEntity) {
        if (courseEntity == null) {
            throw new CourseNotFoundException("Course not found");
        }

        return DashboardCourseResponseDTO.builder()
                .id(courseEntity.getId())
                .title(courseEntity.getTitle())
                .description(courseEntity.getDescription())
                .thumbnailUrl(s3Service.generatePermanentUrl(courseEntity.getThumbnail()))
                .category(courseEntity.getCategoryEntity().getName())
                .instructorName("CourseHub")
                .totalDuration(lessonService.calculateTotalDurationByCourseId(courseEntity.getId()))
                .totalLessons(lessonService.countLessonsByCourseId(courseEntity.getId()))
                .completed(enrollmentEntity.getIsCompleted() == 1L)
                .enrollDate(enrollmentEntity.getCreatedDate())
                .completedDate(enrollmentEntity.getCompletedDate())
                .progress(enrollmentEntity.getProgressPercentage())
                .build();
    }

    @Override
    public Page<CourseResponseDTO> advancedSearch(CourseSearchRequestDTO searchRequest, Pageable pageable) {
        log.info("Performing advanced search with filters - {}", searchRequest);

        // Validate price range
        searchRequest.validatePriceRange();

        // Set default values if not provided
        if (searchRequest.getSortBy() == null) {
            searchRequest.setSortBy(DEFAULT_SORT_BY);
        }
        if (searchRequest.getSortDirection() == null) {
            searchRequest.setSortDirection(DEFAULT_SORT_DIRECTION);
        }

        Page<CourseEntity> courseEntities = searchRepository.advancedSearch(searchRequest, pageable);

        if (courseEntities.isEmpty()) {
            log.warn("No courses found with applied search filters");
        } else {
            log.info("Found {} courses with applied search filters", courseEntities.getTotalElements());
        }

        return courseConverter.toResponseDTOPage(courseEntities);
    }

    @Override
    public CourseSearchStatsResponseDTO getSearchStatistics() {
        log.info("Calculating search statistics");

        // Get all courses for statistics
        List<CourseEntity> allCourses = courseRepository.findAll();

        // Calculate total courses
        long totalCourses = allCourses.size();

        // Calculate price range
        Long minPrice = allCourses.stream()
                .mapToLong(course -> course.getPrice().longValue())
                .min()
                .orElse(0L);

        Long maxPrice = allCourses.stream()
                .mapToLong(course -> course.getPrice().longValue())
                .max()
                .orElse(0L);

        // Calculate average rating
        Long avgRating = allCourses.stream()
                .mapToLong(course -> reviewService.getAverageRating(course.getId()).longValue())
                .sum() / (totalCourses > 0 ? totalCourses : 1);

        // Calculate level statistics
        Map<String, Long> levelStats = allCourses.stream()
                .collect(Collectors.groupingBy(
                        course -> course.getLevel().getLevelName(),
                        Collectors.counting()
                ));

        CourseSearchStatsResponseDTO stats = CourseSearchStatsResponseDTO.builder()
                .totalCourses(totalCourses)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .avgRating(avgRating)
                .levelStats(levelStats)
                .build();

        log.info("Search statistics calculated successfully");
        return stats;
    }

    @Override
    public List<CourseResponseDTO> getCoursesRecommend() {
        log.info("Course recommendations: ");
        List<CourseEntity> courseRecommend = courseRepository.getCoursesRecommend();

        return courseConverter.toResponseDTOList(courseRecommend);
    }

}