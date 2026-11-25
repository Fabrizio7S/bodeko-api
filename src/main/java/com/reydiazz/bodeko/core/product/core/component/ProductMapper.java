package com.reydiazz.bodeko.core.product.core.component;

import com.reydiazz.bodeko.core.product.core.model.entity.Product;
import com.reydiazz.bodeko.core.product.core.web.response.ProductResponse;
import com.reydiazz.bodeko.shared.storage.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ImageStorageService imageStorageService;

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getName(),
                product.getDescription(),
                product.getBasePrice(),
                imageStorageService.toPublicUrl(product.getImageUrl()),
                imageStorageService.toRelativePath(product.getImageUrl()),
                product.isTaxExempt(),
                product.getStatus(),
                product.getCreatedAt()
        );
    }

}
