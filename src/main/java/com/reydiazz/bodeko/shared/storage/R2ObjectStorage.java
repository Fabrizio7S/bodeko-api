package com.reydiazz.bodeko.shared.storage;

import com.reydiazz.bodeko.shared.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/** Implementación sobre la API S3 de Cloudflare R2. */
@Slf4j
public class R2ObjectStorage implements ObjectStorage {

    private static final String CACHE_CONTROL = "public, max-age=31536000, immutable";

    private final S3Client client;
    private final String bucket;

    public R2ObjectStorage(S3Client client, String bucket) {
        this.client = client;
        this.bucket = bucket;
    }

    @Override
    public void put(String key, byte[] content, String contentType) {
        try {
            client.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(contentType)
                            .contentLength((long) content.length)
                            .cacheControl(CACHE_CONTROL)
                            .build(),
                    RequestBody.fromBytes(content));
        } catch (SdkException exception) {
            log.error("No se pudo subir el objeto {} a R2", key, exception);
            throw new BusinessException(StorageErrorCode.STORAGE_UNAVAILABLE);
        }
    }

    @Override
    public void delete(String key) {
        try {
            client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (SdkException exception) {
            // Un objeto huérfano no debe romper la operación del usuario.
            log.warn("No se pudo eliminar el objeto {} de R2", key, exception);
        }
    }
}
