package com.reydiazz.bodeko.core.inventory.model.entity;

import com.reydiazz.bodeko.core.product.core.model.entity.Product;
import com.reydiazz.bodeko.core.product.variant.model.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Table(name = "inventario")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Inventory {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Product product;

    @Column(name = "variante_id", nullable = false, unique = true, updatable = false)
    private UUID variantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variante_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductVariant variant;

    @Builder.Default
    @Column(name = "cantidad", nullable = false)
    private int quantity = 0;

    @Builder.Default
    @Column(name = "umbral_stock_bajo",nullable = false)
    private Integer lowStockThreshold = 5;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime updatedAt;

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
        this.updatedAt = OffsetDateTime.now();
    }

    public void decreaseQuantity(int amount) {
        this.quantity = this.quantity - amount;
        this.updatedAt = OffsetDateTime.now();
    }

    public void increaseQuantity(int amount) {
        this.quantity = this.quantity + amount;
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
        this.updatedAt = OffsetDateTime.now();
    }

    public boolean isLowStock() {
        return quantity <= lowStockThreshold;
    }

}
