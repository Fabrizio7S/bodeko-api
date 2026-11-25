package com.reydiazz.bodeko.shared.storage;

import com.github.f4b6a3.uuid.UuidCreator;
import com.reydiazz.bodeko.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Sube imágenes a R2 y traduce entre la ruta relativa que se guarda en la base de datos
 * (p. ej. {@code stores/{storeId}/products/{uuid}.webp}) y la URL pública que ve el cliente.
 * Así, cambiar de dominio o de proveedor solo requiere cambiar R2_PUBLIC_URL.
 */
@Service
@RequiredArgsConstructor
public class ImageStorageService {

    public static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;

    private static final Pattern PRODUCT_IMAGE_PATH = Pattern.compile(
            "^stores/[0-9a-f-]{36}/products/[0-9a-f-]{36}\\.(?:jpg|png|webp|gif)$");

    private final ObjectStorage objectStorage;
    private final R2StorageProperties properties;

    /** Valida la imagen por su contenido (no por la extensión declarada) y la sube a R2. */
    public UploadedImageResponse uploadProductImage(UUID storeId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(StorageErrorCode.IMAGE_REQUIRED);
        }
        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new BusinessException(StorageErrorCode.IMAGE_TOO_LARGE);
        }
        byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException exception) {
            throw new BusinessException(StorageErrorCode.IMAGE_REQUIRED);
        }
        ImageType type = ImageType.detect(content);
        if (type == null) {
            throw new BusinessException(StorageErrorCode.IMAGE_UNSUPPORTED_TYPE);
        }
        String path = "stores/%s/products/%s.%s".formatted(storeId, UuidCreator.getTimeOrderedEpoch(), type.extension);
        objectStorage.put(path, content, type.contentType);
        return new UploadedImageResponse(path, toPublicUrl(path), type.contentType, content.length);
    }

    /**
     * Normaliza lo que llega en {@code imageUrl}: una URL del propio bucket se reduce a su ruta
     * relativa, y una ruta relativa solo se acepta si pertenece a la tienda.
     * Las URL externas http/https se mantienen por compatibilidad con datos previos.
     */
    public String normalizeProductImage(UUID storeId, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String candidate = value.trim();
        String publicBase = properties.normalizedPublicUrl();
        if (publicBase != null && candidate.startsWith(publicBase + "/")) {
            candidate = candidate.substring(publicBase.length() + 1);
        }
        if (isAbsoluteUrl(candidate)) {
            return candidate;
        }
        if (!PRODUCT_IMAGE_PATH.matcher(candidate).matches()
                || !candidate.startsWith("stores/" + storeId + "/")) {
            throw new BusinessException(StorageErrorCode.IMAGE_PATH_INVALID);
        }
        return candidate;
    }

    /** Convierte la ruta guardada en una URL pública. Las URL absolutas antiguas se devuelven igual. */
    public String toPublicUrl(String storedValue) {
        if (storedValue == null || storedValue.isBlank() || isAbsoluteUrl(storedValue)) {
            return storedValue;
        }
        String publicBase = properties.normalizedPublicUrl();
        return publicBase == null ? storedValue : publicBase + "/" + storedValue;
    }

    /** Devuelve la ruta relativa si la imagen está en R2, o null si es una URL externa. */
    public String toRelativePath(String storedValue) {
        return storedValue == null || storedValue.isBlank() || isAbsoluteUrl(storedValue) ? null : storedValue;
    }

    /** Elimina la imagen anterior de R2 solo cuando la transacción que la reemplazó se confirma. */
    public void deleteReplacedImage(String previous, String current) {
        String previousPath = toRelativePath(previous);
        if (previousPath == null || Objects.equals(previousPath, current)) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    objectStorage.delete(previousPath);
                }
            });
        } else {
            objectStorage.delete(previousPath);
        }
    }

    public static boolean isAbsoluteUrl(String value) {
        String lower = value.trim().toLowerCase(Locale.ROOT);
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    /** Solo URL http/https públicas, sin credenciales. */
    public static boolean isValidExternalUrl(String value) {
        try {
            URI uri = URI.create(value.trim());
            return uri.getHost() != null && uri.getUserInfo() == null
                    && ("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()));
        } catch (IllegalArgumentException error) {
            return false;
        }
    }

    private enum ImageType {
        JPEG("jpg", "image/jpeg"),
        PNG("png", "image/png"),
        WEBP("webp", "image/webp"),
        GIF("gif", "image/gif");

        private final String extension;
        private final String contentType;

        ImageType(String extension, String contentType) {
            this.extension = extension;
            this.contentType = contentType;
        }

        /** Detecta el formato por sus bytes iniciales (firma del archivo). */
        static ImageType detect(byte[] data) {
            if (startsWith(data, 0, 0xFF, 0xD8, 0xFF)) {
                return JPEG;
            }
            if (startsWith(data, 0, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)) {
                return PNG;
            }
            if (startsWith(data, 0, 'R', 'I', 'F', 'F') && startsWith(data, 8, 'W', 'E', 'B', 'P')) {
                return WEBP;
            }
            if (startsWith(data, 0, 'G', 'I', 'F', '8', '7', 'a') || startsWith(data, 0, 'G', 'I', 'F', '8', '9', 'a')) {
                return GIF;
            }
            return null;
        }

        private static boolean startsWith(byte[] data, int offset, int... signature) {
            if (data.length < offset + signature.length) {
                return false;
            }
            for (int i = 0; i < signature.length; i++) {
                if ((data[offset + i] & 0xFF) != signature[i]) {
                    return false;
                }
            }
            return true;
        }
    }
}
