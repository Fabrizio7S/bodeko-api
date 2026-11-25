package com.reydiazz.bodeko.shared.storage;

/**
 * @param path        ruta relativa que se envía en {@code imageUrl} al crear o editar el producto.
 * @param url         URL pública calculada con R2_PUBLIC_URL, solo para previsualizar.
 * @param contentType tipo MIME detectado por el contenido del archivo.
 * @param size        tamaño en bytes.
 */
public record UploadedImageResponse(String path, String url, String contentType, long size) {
}
