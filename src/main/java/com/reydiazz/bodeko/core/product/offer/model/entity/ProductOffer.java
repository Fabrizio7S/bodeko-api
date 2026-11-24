package com.reydiazz.bodeko.core.product.offer.model.entity;

import com.reydiazz.bodeko.core.product.core.model.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@IdClass(ProductOfferId.class)
@Table(
        name = "ofertas_producto",
        indexes = {
                @Index(name = "idx_product_offers_store_id", columnList = "tienda_id")
        }
)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductOffer {

    @Id
    @Column(name = "producto_id", nullable = false, updatable = false)
    private UUID productId;

    @Id
    @Column(name = "inicia_en", nullable = false, updatable = false)
    private OffsetDateTime startsAt;

    @Id
    @Column(name = "termina_en", nullable = false, updatable = false)
    private OffsetDateTime endsAt;

    @Column(name = "tienda_id", nullable = false, updatable = false)
    private UUID storeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(
                    name = "producto_id",
                    referencedColumnName = "id",
                    insertable = false,
                    updatable = false
            ),
            @JoinColumn(
                    name = "tienda_id",
                    referencedColumnName = "tienda_id",
                    insertable = false,
                    updatable = false
            )
    })
    private Product product;

    @Column(name = "precio_oferta", nullable = false, precision = 12, scale = 2)
    private BigDecimal offerPrice;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "porcentaje_descuento", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    /** Calcula sobre el precio vigente; el mínimo monetario es un céntimo. */
    public BigDecimal getOfferPrice() {
        return discountPercentage == null ? offerPrice : percentagePrice(product.getBasePrice(), discountPercentage);
    }

    public static BigDecimal percentagePrice(BigDecimal base, BigDecimal percentage) {
        return base.multiply(BigDecimal.ONE.subtract(percentage.movePointLeft(2)))
            .setScale(2, java.math.RoundingMode.HALF_UP).max(new BigDecimal("0.01"));
    }

    public boolean isActiveAt(OffsetDateTime moment) {
        return !moment.isBefore(startsAt) && moment.isBefore(endsAt);
    }

}
