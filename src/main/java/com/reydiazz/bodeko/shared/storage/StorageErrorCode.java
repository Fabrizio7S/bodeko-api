package com.reydiazz.bodeko.shared.storage;

import com.reydiazz.bodeko.shared.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StorageErrorCode implements ErrorCode {

    IMAGE_REQUIRED("IMAGE_REQUIRED", "Adjunta una imagen en el campo 'file'.", HttpStatus.BAD_REQUEST),
    IMAGE_TOO_LARGE("IMAGE_TOO_LARGE", "La imagen supera el tamaño máximo de 5 MB.", HttpStatus.PAYLOAD_TOO_LARGE),
    IMAGE_UNSUPPORTED_TYPE("IMAGE_UNSUPPORTED_TYPE", "Solo se admiten imágenes JPG, PNG, WEBP o GIF.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    IMAGE_PATH_INVALID("IMAGE_PATH_INVALID", "La imagen debe ser una ruta subida a esta tienda o una URL http/https válida.", HttpStatus.BAD_REQUEST),
    STORAGE_NOT_CONFIGURED("STORAGE_NOT_CONFIGURED", "El almacenamiento de imágenes no está configurado.", HttpStatus.SERVICE_UNAVAILABLE),
    STORAGE_UNAVAILABLE("STORAGE_UNAVAILABLE", "No se pudo guardar la imagen. Inténtalo nuevamente.", HttpStatus.BAD_GATEWAY);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

}
