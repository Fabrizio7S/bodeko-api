package com.reydiazz.bodeko.core.product.core.model.entity;

import com.reydiazz.bodeko.core.category.model.Category;
import com.reydiazz.bodeko.core.product.core.model.enums.ProductStatus;
import com.reydiazz.bodeko.core.store.model.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Table(
        name = "productos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_products_id_store",
                        columnNames = {"id", "tienda_id"}
                )
        }
)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tienda_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "categoria_id",
            referencedColumnName = "id"
    )
    private Category category;

    @Column(name = "nombre",nullable = false)
    private String name;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;

    @Column(name = "precio_base",nullable = false)
    private BigDecimal basePrice;

    @Column(name = "url_imagen", length = 500)
    private String imageUrl;

    @Builder.Default
    @Column(name = "exonerado_impuesto", nullable = false)
    private boolean isTaxExempt = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public void update(
            String name,
            String description,
            BigDecimal basePrice,
            String imageUrl,
            boolean isTaxExempt,
            Category category
    ) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.imageUrl = imageUrl;
        this.isTaxExempt = isTaxExempt;
        this.category = category;
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

}
