package com.reydiazz.bodeko.core.store.model.entity;

import com.reydiazz.bodeko.core.store.model.enums.TypeCurrency;
import com.reydiazz.bodeko.core.user.core.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Table(name = "tiendas")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Store {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "propietario_id", nullable = false)
    private User owner;

    @Column(name = "nombre",nullable = false)
    private String name;

    @Column(name = "ruc", nullable = false)
    private String taxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emisor_facturacion_id")
    private com.reydiazz.bodeko.core.billing.model.BillingIssuer billingIssuer;

    @Column(name = "razon_social",nullable = false)
    private String legalName;

    @Column(name = "subdominio",nullable = false,unique = true)
    private String subdomain;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "moneda", nullable = false, length = 3)
    private TypeCurrency currency = TypeCurrency.PEN;

    @Builder.Default
    @Column(name = "tasa_impuesto", nullable = false, precision = 4, scale = 2)
    private BigDecimal taxRate = new BigDecimal("0.18");

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public void updateTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /** Vincula el emisor fiscal compartido y conserva sus datos en los campos existentes. */
    public void setBillingIssuer(com.reydiazz.bodeko.core.billing.model.BillingIssuer issuer) {
        this.billingIssuer = issuer;
        this.taxId = issuer.getTaxId();
        this.legalName = issuer.getLegalName();
    }

}
