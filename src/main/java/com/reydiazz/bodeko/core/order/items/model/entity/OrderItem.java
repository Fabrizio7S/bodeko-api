package com.reydiazz.bodeko.core.order.items.model.entity;

import com.reydiazz.bodeko.core.order.core.model.entity.Order;
import com.reydiazz.bodeko.core.product.core.model.entity.Product;
import com.reydiazz.bodeko.core.product.variant.model.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Builder
@Entity
@Getter
@IdClass(OrderItemId.class)
@Table(name = "items_orden")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem {

    @Id
    @Column(name = "orden_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "producto_id", nullable = false, updatable = false)
    private UUID productId;

    @Id
    @Column(name = "variante_id", nullable = false, updatable = false)
    private UUID variantId;

    @Column(name = "tienda_id", nullable = false, updatable = false)
    private UUID storeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variante_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductVariant variant;

    @Column(name = "nombre_producto", nullable = false)
    private String productName;

    @Column(name = "sku_variante", nullable = false, length = 80, updatable = false)
    private String variantSku;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "opciones_variante", nullable = false, columnDefinition = "jsonb", updatable = false)
    private Map<String, String> variantOptions;

    @Column(name = "cantidad", nullable = false)
    private Integer quantity;

    @Column(name = "precio_unitario_lista", nullable = false, precision = 12, scale = 2)
    private BigDecimal listUnitPrice;

    @Column(name = "precio_unitario_cobrado", nullable = false, precision = 12, scale = 2)
    private BigDecimal chargedUnitPrice;

    @Column(name = "subtotal_item", nullable = false, precision = 12, scale = 2)
    private BigDecimal itemSubtotal;
}
