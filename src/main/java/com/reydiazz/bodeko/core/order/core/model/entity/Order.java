package com.reydiazz.bodeko.core.order.core.model.entity;

import com.reydiazz.bodeko.core.order.core.model.enums.CustomerDocumentType;
import com.reydiazz.bodeko.core.order.core.model.enums.InvoiceType;
import com.reydiazz.bodeko.core.order.core.model.enums.OrderStatus;
import com.reydiazz.bodeko.core.store.model.entity.Store;
import com.reydiazz.bodeko.core.customer.model.entity.Customer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Table(
        name = "ordenes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_orders_id_store",
                        columnNames = {"id", "tienda_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_orders_store_status",
                        columnList = "tienda_id, estado"
                ),
                @Index(
                        name = "idx_orders_customer_id",
                        columnList = "cliente_id"
                ),
                @Index(
                        name = "idx_orders_created_at",
                        columnList = "creado_en"
                )
        }
)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "tienda_id",nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id",nullable = false)
    private Customer customer;

    @Column(name = "numero_orden",nullable = false,unique = true)
    private String orderNumber;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante",nullable = false)
    private InvoiceType invoiceType = InvoiceType.RECEIPT;

    @Enumerated(EnumType.STRING)
    @Column(name = "cliente_tipo_documento", nullable = false)
    private CustomerDocumentType customerDocumentType;

    @Column(name = "cliente_numero_documento", nullable = false)
    private String customerDocumentNumber;

    @Column(name = "cliente_razon_social")
    private String customerBusinessName;

    @Column(name = "cliente_direccion_facturacion")
    private String customerBillingAddress;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "monto_impuesto", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "tasa_impuesto_aplicada", nullable = false, precision = 4, scale = 2)
    private BigDecimal appliedTaxRate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "transportista", length = 100)
    private String trackingCarrier;

    @Column(name = "numero_seguimiento", length = 120)
    private String trackingNumber;

    @Column(name = "url_seguimiento", length = 500)
    private String trackingUrl;

    @Column(name = "entrega_estimada_en")
    private OffsetDateTime estimatedDeliveryAt;

    public void markAsPaid() {
        this.status = OrderStatus.PAID;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    /** Actualiza los datos de seguimiento del envío. */
    public void updateTracking(String carrier, String number, String url, OffsetDateTime estimatedDeliveryAt) {
        this.trackingCarrier = carrier;
        this.trackingNumber = number;
        this.trackingUrl = url;
        this.estimatedDeliveryAt = estimatedDeliveryAt;
    }

}
