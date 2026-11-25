package com.reydiazz.bodeko.core.product.core.web.response;

import com.reydiazz.bodeko.core.product.core.model.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID categoryId,
        String name,
        String description,
        BigDecimal basePrice,
        /** URL pública lista para mostrar (calculada con R2_PUBLIC_URL). */
        String imageUrl,
        /** Ruta relativa guardada en la base de datos; null si es una URL externa antigua. */
        String imagePath,
        boolean taxExempt,
        ProductStatus status,
        OffsetDateTime createdAt
) {
}
