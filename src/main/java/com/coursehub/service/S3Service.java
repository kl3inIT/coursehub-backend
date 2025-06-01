package com.coursehub.service;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public interface S3Service {

    /**
     * Generates a pre-signed URL for temporary access (for videos)
     * @param objectKey The key of the object in S3
     * @return A pre-signed URL that expires after a set duration
     */
    String generatePresignedUrl(String objectKey);

    /**
     * Generates a permanent public URL for thumbnails
     * @param objectKey The key of the object in S3
     * @return A permanent public URL
     */
    String generatePermanentUrl(String objectKey);

    /**
     * Gets an S3 object as a stream
     * @param objectKey The key of the object in S3
     * @return A response input stream for the object
     */
    ResponseInputStream<GetObjectResponse> getObject(String objectKey);

    /**
     * Uploads a file to S3
     * @param objectKey The desired key for the object
     * @param contentType The content type of the file
     * @param fileBytes The file content as byte array
     * @return The object key of the uploaded file
     */
    String uploadFile(String objectKey, String contentType, byte[] fileBytes);
    
    /**
     * Deletes an object from S3
     * @param objectKey The key of the object to delete
     */
    void deleteObject(String objectKey);

}
