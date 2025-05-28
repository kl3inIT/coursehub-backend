package com.coursehub.service.impl;

import com.coursehub.converter.CourseConverter;
import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.exception.course.*;
import com.coursehub.repository.CourseRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.CourseService;
import com.coursehub.service.S3Service;
import com.coursehub.utils.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseConverter courseConverter;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    // Allowed file types for thumbnails
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO) {
        log.info("Creating new course: {}", courseRequestDTO.getTitle());

        try {
            CourseEntity courseEntity = courseConverter.toEntity(courseRequestDTO);
            
            // Create mock user (you might want to get actual user from context)
            UserEntity mockUser = new UserEntity();
            mockUser.setEmail("test@example.com");
            mockUser.setPassword("123456");
            mockUser.setName("Test User");
            mockUser.setAvatar("https://example.com/avatar.png");
            mockUser.setIsActive(1L);
            userRepository.save(mockUser);
            
            courseEntity.setUser(mockUser);
            
            CourseEntity savedCourse = courseRepository.save(courseEntity);
            log.info("Successfully created course with ID: {}", savedCourse.getId());
            
            return courseConverter.toResponseDTO(savedCourse);
            
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
            String objectKey = String.format("public/thumbnails/courses/%d/%s", courseId, file.getOriginalFilename());
            
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
}