package com.coursehub.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import com.coursehub.service.S3Service;
import java.time.Duration;
import java.util.Map;

@Service
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    public S3ServiceImpl(
            S3Client s3Client,
            S3Presigner s3Presigner,
            @Value("${spring.aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
        log.info("Initializing S3Service with bucket: {}", bucketName);
    }

    /**
     * Generates a pre-signed URL for the specified S3 object
     * @param objectKey The key of the object in S3
     * @return A pre-signed URL that can be used to access the object temporarily
     */
    @Override
    public String generatePresignedUrl(String objectKey) {
        log.debug("Generating presigned URL for object: {}", objectKey);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    /**
     * Generates a permanent public URL for thumbnails using direct S3 URL
     * @param objectKey The key of the object in S3
     * @return A permanent public URL
     */
    @Override
    public String generatePermanentUrl(String objectKey) {
        log.debug("Generating permanent URL for object: {}", objectKey);
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, objectKey);
    }

    /**
     * Gets an S3 object as a stream
     * @param objectKey The key of the object in S3
     * @return A response input stream for the object
     */
    @Override
    public ResponseInputStream<GetObjectResponse> getObject(String objectKey) {
        log.debug("Getting object from S3: {}", objectKey);
        
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
                
        return s3Client.getObject(getObjectRequest);
    }

    /**
     * Uploads a file to S3 with public-read access for thumbnails
     * @param objectKey The desired key for the object
     * @param contentType The content type of the file
     * @param fileBytes The file content as byte array
     * @return The object key of the uploaded file
     */
    @Override
    public String uploadFile(String objectKey, String contentType, byte[] fileBytes) {
        log.debug("Uploading file to S3 with key: {}", objectKey);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));
        log.info("Successfully uploaded file: {}", objectKey);
        return objectKey;
    }

    /**
     * Deletes an object from S3
     * @param objectKey The key of the object to delete
     */
    @Override
    public void deleteObject(String objectKey) {
        log.debug("Deleting object from S3: {}", objectKey);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted object: {}", objectKey);
            
        } catch (S3Exception e) {
            log.error("Failed to delete object from S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete object from S3: " + e.getMessage(), e);
        }
    }

    /**
     * Gets metadata for an S3 object
     * @param objectKey The key of the object in S3
     * @return A map of the object's metadata
     */
    public Map<String, String> getObjectMetadata(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        GetObjectResponse response = s3Client.getObject(getObjectRequest).response();
        return response.metadata();
    }

}
