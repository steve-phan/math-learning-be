package com.mathlearning.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@Slf4j
public class StorageConfig {

    @Value("${app.storage.type}")
    private String storageType;

    @Value("${app.storage.minio.endpoint}")
    private String minioEndpoint;

    @Value("${app.storage.minio.access-key}")
    private String minioAccessKey;

    @Value("${app.storage.minio.secret-key}")
    private String minioSecretKey;

    @Value("${app.storage.aws.region:us-east-1}")
    private String awsRegion;

    @Bean
    public S3Client s3Client() {
        if ("minio".equalsIgnoreCase(storageType)) {
            log.info("Configuring MinIO S3 client at: {}", minioEndpoint);

            return S3Client.builder()
                    .endpointOverride(URI.create(minioEndpoint))
                    .region(Region.US_EAST_1) // MinIO doesn't care about region
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(minioAccessKey, minioSecretKey)))
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .build())
                    .build();
        } else {
            log.info("Configuring AWS S3 client in region: {}", awsRegion);

            return S3Client.builder()
                    .region(Region.of(awsRegion))
                    .build();
        }
    }
}
