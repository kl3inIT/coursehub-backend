package com.coursehub.service;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public interface S3Service {

    String generatePermanentUrl(String objectKey);

    String generatePresignedGetUrl(String objectKey);

    String generatePresignedPutUrl(String objectKey, String contentType);

    ResponseInputStream<GetObjectResponse> getObject(String objectKey);

    String uploadFile(String objectKey, String contentType, byte[] fileBytes);

    void deleteObject(String objectKey);

}
