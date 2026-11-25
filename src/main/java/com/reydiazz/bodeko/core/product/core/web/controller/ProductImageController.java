package com.reydiazz.bodeko.core.product.core.web.controller;

import com.reydiazz.bodeko.core.product.core.util.ProductRouters;
import com.reydiazz.bodeko.shared.storage.ImageStorageService;
import com.reydiazz.bodeko.shared.storage.UploadedImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Sube la imagen a R2 y devuelve su ruta relativa. El cliente envía esa ruta en
 * {@code imageUrl} al crear o editar el producto.
 */
@RestController
@RequestMapping(ProductRouters.API_BASE)
@RequiredArgsConstructor
public class ProductImageController {

    private final ImageStorageService imageStorageService;

    @PostMapping(value = ProductRouters.IMAGES, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadedImageResponse> upload(
            @PathVariable UUID storeId,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        UploadedImageResponse response = imageStorageService.uploadProductImage(storeId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
