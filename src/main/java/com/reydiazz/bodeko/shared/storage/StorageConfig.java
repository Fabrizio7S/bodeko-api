package com.reydiazz.bodeko.shared.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Slf4j
@Configuration
@EnableConfigurationProperties(R2StorageProperties.class)
public class StorageConfig {

    /** R2 solo acepta la región "auto". */
    private static final Region R2_REGION = Region.of("auto");

    @Bean
    public ObjectStorage objectStorage(R2StorageProperties properties) {
        if (!properties.isConfigured()) {
            log.warn("Cloudflare R2 no está configurado (variables R2_*). La subida de imágenes queda deshabilitada.");
            return new UnconfiguredObjectStorage();
        }
        S3Client client = S3Client.builder()
                .endpointOverride(URI.create(properties.resolvedEndpoint()))
                .region(R2_REGION)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        properties.accessKeyId().trim(), properties.secretAccessKey().trim())))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
        return new R2ObjectStorage(client, properties.bucket().trim());
    }

}
