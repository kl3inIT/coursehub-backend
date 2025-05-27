package com.coursehub.service.impl;

import com.coursehub.converter.CourseConverter;
import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.exception.ResourceNotFoundException;
import com.coursehub.repository.CourseRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.CourseService;
import com.coursehub.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseConverter courseConverter;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    // Business Logic Methods

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getFeaturedCourses() {
        log.info("Fetching featured courses");

        List<CourseEntity> featuredCourses = courseRepository.findFeaturedCourses();

        // Limit to 4 courses for featured section
        List<CourseEntity> topFourCourses = featuredCourses.stream()
                .limit(4)
                .toList();

        return courseConverter.toResponseDTOList(topFourCourses);
    }

    @Override
    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO) {
        log.info("Creating new course: {}", courseRequestDTO.getTitle());

        CourseEntity courseEntity = courseConverter.toEntity(courseRequestDTO);
        UserEntity mockUser = new UserEntity();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("123456");
        mockUser.setName("Test User");
        mockUser.setAvatarUrl("https://example.com/avatar.png"); // hoặc null nếu nullable
        mockUser.setIsActive(true);
        userRepository.save(mockUser);
        courseEntity.setUser(mockUser); // Set instructor from request
        // Generate unique course code

        CourseEntity savedCourse = courseRepository.save(courseEntity);
        return courseConverter.toResponseDTO(savedCourse);
    }

    public String uploadThumbnail(Long courseId, MultipartFile file) throws IOException {
        String objectKey = String.format("thumbnails/courses/%d/%s", courseId, file.getOriginalFilename());
        String thubmnailKey = s3Service.uploadFile(objectKey, file.getContentType(), file.getBytes());
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        course.setThumbnail(thubmnailKey);
        return thubmnailKey;
    }

}