package com.coursehub.utils;

import com.coursehub.exception.course.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class FileValidationUtil {

    // Image validation constants
    public static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    // Video validation constants
    public static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
        "video/mp4", "video/avi", "video/mov", "video/wmv", "video/flv", "video/webm"
    );
    
    // Document validation constants
    public static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
        "application/pdf", "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    );
    
    // Size limits (in bytes)
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB
    public static final long MAX_DOCUMENT_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Validates image files (for thumbnails, avatars, etc.)
     */
    public static void validateImageFile(MultipartFile file) {
        validateImageFile(file, MAX_IMAGE_SIZE);
    }
    
    public static void validateImageFile(MultipartFile file, long maxSize) {
        log.debug("Validating image file: {}", file.getOriginalFilename());
        
        validateBasicFile(file);
        validateFileSize(file, maxSize);
        validateContentType(file, ALLOWED_IMAGE_TYPES, "image");
    }

    /**
     * Validates video files (for course lessons)
     */
    public static void validateVideoFile(MultipartFile file) {
        validateVideoFile(file, MAX_VIDEO_SIZE);
    }
    
    public static void validateVideoFile(MultipartFile file, long maxSize) {
        log.debug("Validating video file: {}", file.getOriginalFilename());
        
        validateBasicFile(file);
        validateFileSize(file, maxSize);
        validateContentType(file, ALLOWED_VIDEO_TYPES, "video");
    }

    /**
     * Validates document files (for course materials)
     */
    public static void validateDocumentFile(MultipartFile file) {
        validateDocumentFile(file, MAX_DOCUMENT_SIZE);
    }
    
    public static void validateDocumentFile(MultipartFile file, long maxSize) {
        log.debug("Validating document file: {}", file.getOriginalFilename());
        
        validateBasicFile(file);
        validateFileSize(file, maxSize);
        validateContentType(file, ALLOWED_DOCUMENT_TYPES, "document");
    }

    /**
     * Basic file validation (null, empty, filename)
     */
    private static void validateBasicFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File cannot be null or empty");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new InvalidFileException("Filename cannot be empty");
        }
        
        // Check for path traversal attacks
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new InvalidFileException("Invalid filename: contains illegal characters");
        }
        
        // Check filename length
        if (originalFilename.length() > 255) {
            throw new InvalidFileException("Filename too long (maximum 255 characters)");
        }
    }

    /**
     * Validates file size
     */
    private static void validateFileSize(MultipartFile file, long maxSize) {
        if (file.getSize() > maxSize) {
            String maxSizeStr = formatFileSize(maxSize);
            String actualSizeStr = formatFileSize(file.getSize());
            throw new InvalidFileException(
                String.format("File size (%s) exceeds maximum allowed size (%s)", actualSizeStr, maxSizeStr)
            );
        }
        
        if (file.getSize() == 0) {
            throw new InvalidFileException("File is empty");
        }
    }

    /**
     * Validates content type
     */
    private static void validateContentType(MultipartFile file, List<String> allowedTypes, String fileTypeDescription) {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new InvalidFileException("Cannot determine file type");
        }

        if (!allowedTypes.contains(contentType.toLowerCase())) {
            throw new InvalidFileException(
                String.format("Invalid %s file type: %s. Allowed types: %s",
                    fileTypeDescription, contentType, String.join(", ", allowedTypes))
            );
        }
    }


    /**
     * Gets expected content type based on file extension
     */
    private static String getExpectedContentType(String extension) {
        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "mp4" -> "video/mp4";
            case "avi" -> "video/avi";
            case "mov" -> "video/mov";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default -> null;
        };
    }

    /**
     * Formats file size in human readable format
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Validates multiple files at once
     */
    public static void validateImageFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new InvalidFileException("No files provided");
        }
        
        for (int i = 0; i < files.size(); i++) {
            try {
                validateImageFile(files.get(i));
            } catch (InvalidFileException e) {
                throw new InvalidFileException(String.format("File %d: %s", i + 1, e.getMessage()));
            }
        }
    }

    /**
     * Checks if file is an image
     */
    public static boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
    }

    /**
     * Checks if file is a video
     */
    public static boolean isVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase());
    }

    /**
     * Checks if file is a document
     */
    public static boolean isDocumentFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_DOCUMENT_TYPES.contains(contentType.toLowerCase());
    }
} 