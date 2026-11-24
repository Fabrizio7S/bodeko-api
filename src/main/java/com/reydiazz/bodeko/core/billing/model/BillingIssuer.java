package com.reydiazz.bodeko.core.billing.model;

import com.reydiazz.bodeko.core.user.core.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Perfil fiscal reutilizable por las tiendas de un mismo propietario. */
@Entity
@Table(name = "emisores_facturacion", uniqueConstraints = @UniqueConstraint(
        name = "uq_billing_issuer_owner_tax_id", columnNames = {"propietario_id", "ruc"}))
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BillingIssuer {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "propietario_id", nullable = false, updatable = false)
    private User owner;

    @Column(name = "ruc", nullable = false, length = 11)
    private String taxId;

    @Column(name = "razon_social", nullable = false, length = 200)
    private String legalName;

    @Column(name = "direccion_fiscal", length = 300)
    private String fiscalAddress;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime updatedAt;

    /** Actualiza los datos compartidos por todas las tiendas asociadas. */
    public void update(String taxId, String legalName, String fiscalAddress) {
        this.taxId = taxId;
        this.legalName = legalName;
        this.fiscalAddress = fiscalAddress;
        this.updatedAt = OffsetDateTime.now();
    }
}
