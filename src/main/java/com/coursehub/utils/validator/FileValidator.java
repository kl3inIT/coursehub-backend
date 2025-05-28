package com.coursehub.utils.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class FileValidator {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MIN_FILE_SIZE = 1024; // 1KB

    /**
     * Validates if the uploaded file is a valid image for thumbnail
     * 
     * @param file The multipart file to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateThumbnailFile(MultipartFile file) {
        validateFileNotNull(file);
        validateFileNotEmpty(file);
        validateFileSize(file);
        validateImageType(file);
        validateFileName(file);
    }

    private void validateFileNotNull(MultipartFile file) {
        if (file == null) {
            log.warn("File validation failed: File is null");
            throw new IllegalArgumentException("File cannot be null");
        }
    }

    private void validateFileNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("File validation failed: File is empty");
            throw new IllegalArgumentException("File cannot be empty");
        }
    }

    private void validateFileSize(MultipartFile file) {
        long fileSize = file.getSize();
        
        if (fileSize < MIN_FILE_SIZE) {
            log.warn("File validation failed: File too small. Size: {} bytes", fileSize);
            throw new IllegalArgumentException("File size must be at least 1KB");
        }
        
        if (fileSize > MAX_FILE_SIZE) {
            log.warn("File validation failed: File too large. Size: {} bytes", fileSize);
            throw new IllegalArgumentException("File size cannot exceed 5MB");
        }
    }

    private void validateImageType(MultipartFile file) {
        String contentType = file.getContentType();
        
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            log.warn("File validation failed: Invalid content type: {}", contentType);
            throw new IllegalArgumentException("Only JPEG, PNG, and WebP images are allowed");
        }
    }

    private void validateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            log.warn("File validation failed: Invalid filename");
            throw new IllegalArgumentException("File must have a valid filename");
        }
        
        // Check for potentially dangerous file extensions
        String lowerCaseFilename = originalFilename.toLowerCase();
        if (lowerCaseFilename.contains("..") || lowerCaseFilename.contains("/") || lowerCaseFilename.contains("\\")) {
            log.warn("File validation failed: Suspicious filename: {}", originalFilename);
            throw new IllegalArgumentException("Filename contains invalid characters");
        }
    }
} 