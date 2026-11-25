package com.reydiazz.bodeko.shared.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración de Cloudflare R2 (API compatible con S3).
 *
 * @param accountId       ID de la cuenta de Cloudflare; con él se arma el endpoint si no se indica otro.
 * @param endpoint        Endpoint S3 opcional (p. ej. https://ACCOUNT_ID.r2.cloudflarestorage.com).
 * @param accessKeyId     Access Key ID del token de API de R2.
 * @param secretAccessKey Secret Access Key del token de API de R2.
 * @param bucket          Nombre del bucket donde se guardan las imágenes.
 * @param publicUrl       URL pública del bucket (dominio propio o r2.dev). Solo se usa al responder;
 *                        en la base de datos se guarda la ruta relativa.
 */
@ConfigurationProperties(prefix = "storage.r2")
public record R2StorageProperties(
        String accountId,
        String endpoint,
        String accessKeyId,
        String secretAccessKey,
        String bucket,
        String publicUrl
) {

    public boolean isConfigured() {
        return hasText(resolvedEndpoint()) && hasText(accessKeyId) && hasText(secretAccessKey) && hasText(bucket);
    }

    public String resolvedEndpoint() {
        if (hasText(endpoint)) {
            return endpoint.trim();
        }
        return hasText(accountId) ? "https://" + accountId.trim() + ".r2.cloudflarestorage.com" : null;
    }

    /** URL pública sin barra final, o null si no se configuró. */
    public String normalizedPublicUrl() {
        if (!hasText(publicUrl)) {
            return null;
        }
        String value = publicUrl.trim();
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
