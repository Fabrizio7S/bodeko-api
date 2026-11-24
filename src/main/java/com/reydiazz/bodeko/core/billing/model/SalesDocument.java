package com.reydiazz.bodeko.core.billing.model;

import com.reydiazz.bodeko.core.order.core.model.enums.InvoiceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Registro inmutable de una boleta o factura simulada asociada a una venta pagada. */
@Entity
@Table(name = "documentos_venta", uniqueConstraints = @UniqueConstraint(
        name = "uq_sales_documents_order", columnNames = "orden_id"), indexes = {
        @Index(name = "idx_sales_documents_store_created", columnList = "tienda_id, creado_en")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SalesDocument {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "orden_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "tienda_id", nullable = false, updatable = false)
    private UUID storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emisor_facturacion_id")
    private BillingIssuer issuer;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 20, updatable = false)
    private InvoiceType invoiceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 32)
    private SalesDocumentStatus status;

    @Column(name = "serie", length = 4)
    private String series;

    @Column(name = "numero_documento")
    private Long documentNumber;

    @Column(name = "emisor_ruc", length = 11)
    private String issuerTaxId;

    @Column(name = "emisor_razon_social", length = 200)
    private String issuerLegalName;

    @Column(name = "emisor_direccion_fiscal", length = 300)
    private String issuerFiscalAddress;

    @Column(name = "emitido_en")
    private OffsetDateTime issuedAt;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /** Completa una emisión pendiente después de registrar los datos fiscales. */
    /** Marca como anulado el documento simulado si se reembolsa la venta. */
    public void voidDocument() {
        this.status = SalesDocumentStatus.VOIDED;
    }

    public void issue(BillingIssuer issuer, String series, long number) {
        this.issuer = issuer;
        this.issuerTaxId = issuer.getTaxId();
        this.issuerLegalName = issuer.getLegalName();
        this.issuerFiscalAddress = issuer.getFiscalAddress();
        this.series = series;
        this.documentNumber = number;
        this.issuedAt = OffsetDateTime.now();
        this.status = SalesDocumentStatus.ISSUED;
    }
}
