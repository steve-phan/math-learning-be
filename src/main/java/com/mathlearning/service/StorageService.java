package com.mathlearning.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final S3Client s3Client;

    @Value("${app.storage.minio.bucket-name:mathlearning}")
    private String bucketName;

    public void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info("Bucket '{}' already exists", bucketName);
        } catch (NoSuchBucketException e) {
            log.info("Creating bucket '{}'", bucketName);
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        }
    }

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        ensureBucketExists();

        String fileName = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // Return the file URL
        String fileUrl = String.format("%s/%s/%s",
                s3Client.serviceClientConfiguration().endpointOverride().orElse(null),
                bucketName,
                fileName);

        log.info("File uploaded successfully: {}", fileUrl);
        return fileUrl;
    }

    public String getFileUrl(String fileName) {
        return String.format("%s/%s/%s",
                s3Client.serviceClientConfiguration().endpointOverride().orElse(null),
                bucketName,
                fileName);
    }
}
