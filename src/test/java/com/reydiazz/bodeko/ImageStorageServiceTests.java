package com.reydiazz.bodeko;

import com.reydiazz.bodeko.shared.exception.BusinessException;
import com.reydiazz.bodeko.shared.storage.ImageStorageService;
import com.reydiazz.bodeko.shared.storage.ObjectStorage;
import com.reydiazz.bodeko.shared.storage.R2StorageProperties;
import com.reydiazz.bodeko.shared.storage.UploadedImageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ImageStorageServiceTests {

    private static final String PUBLIC_URL = "https://img.example.test";
    private static final byte[] PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0, 0};

    private final Map<String, byte[]> stored = new HashMap<>();
    private final ObjectStorage fakeStorage = new ObjectStorage() {
        @Override
        public void put(String key, byte[] content, String contentType) {
            stored.put(key, content);
        }

        @Override
        public void delete(String key) {
            stored.remove(key);
        }
    };
    private final ImageStorageService service = new ImageStorageService(fakeStorage,
            new R2StorageProperties("acc", null, "id", "secret", "bucket", PUBLIC_URL + "/"));

    @Test
    void uploadStoresRelativePathAndReturnsPublicUrl() {
        UUID storeId = UUID.randomUUID();
        UploadedImageResponse response = service.uploadProductImage(storeId,
                new MockMultipartFile("file", "photo.jpg", "image/jpeg", PNG));

        assertTrue(response.path().startsWith("stores/" + storeId + "/products/"));
        assertTrue(response.path().endsWith(".png"), "el tipo se detecta por el contenido");
        assertEquals("image/png", response.contentType());
        assertEquals(PUBLIC_URL + "/" + response.path(), response.url());
        assertTrue(stored.containsKey(response.path()));
    }

    @Test
    void rejectsNonImageContent() {
        BusinessException error = assertThrows(BusinessException.class, () -> service.uploadProductImage(
                UUID.randomUUID(), new MockMultipartFile("file", "x.png", "image/png", "<svg/>".getBytes())));
        assertEquals("IMAGE_UNSUPPORTED_TYPE", error.getError().getCode());
    }

    @Test
    void normalizesOwnPublicUrlToRelativePathAndChecksStore() {
        UUID storeId = UUID.randomUUID();
        String path = "stores/" + storeId + "/products/" + UUID.randomUUID() + ".webp";

        assertEquals(path, service.normalizeProductImage(storeId, path));
        assertEquals(path, service.normalizeProductImage(storeId, PUBLIC_URL + "/" + path));
        assertEquals("https://cdn.other.test/a.png", service.normalizeProductImage(storeId, "https://cdn.other.test/a.png"));
        assertThrows(BusinessException.class, () -> service.normalizeProductImage(UUID.randomUUID(), path));
        assertThrows(BusinessException.class, () -> service.normalizeProductImage(storeId, "../secret.png"));
    }

    @Test
    void resolvesPublicUrlOnlyForRelativePaths() {
        assertEquals(PUBLIC_URL + "/stores/a/b.png", service.toPublicUrl("stores/a/b.png"));
        assertEquals("https://legacy.test/x.png", service.toPublicUrl("https://legacy.test/x.png"));
        assertNull(service.toPublicUrl(null));
    }
}
