package com.reydiazz.bodeko.core.cart.items.model.entity;

import com.reydiazz.bodeko.core.product.core.model.entity.Product;
import com.reydiazz.bodeko.core.shopping.cart.model.entity.ShoppingCart;
import com.reydiazz.bodeko.core.product.variant.model.ProductVariant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Builder
@Entity
@Getter
@IdClass(ShoppingCartItemId.class)
@Table(name = "items_carrito_compra")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ShoppingCartItem {

    @Id
    @Column(name = "carrito_id", nullable = false, updatable = false)
    private UUID cartId;

    @Column(name = "producto_id", nullable = false, updatable = false)
    private UUID productId;

    @Id
    @Column(name = "variante_id", nullable = false, updatable = false)
    private UUID variantId;

    @Column(name = "tienda_id", nullable = false, updatable = false)
    private UUID storeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrito_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ShoppingCart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variante_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductVariant variant;

    @Builder.Default
    @Column(name = "cantidad", nullable = false)
    private Integer quantity = 1;

    public void increaseQuantity(int amount) {
        this.quantity = this.quantity + amount;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

}
