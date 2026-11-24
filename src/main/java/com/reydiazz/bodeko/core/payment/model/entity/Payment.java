package com.reydiazz.bodeko.core.payment.model.entity;

import com.reydiazz.bodeko.core.order.core.model.entity.Order;
import com.reydiazz.bodeko.core.payment.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Table(
        name = "pagos",
        indexes = {
                @Index(
                        name = "idx_payments_order_id",
                        columnList = "orden_id"
                )
        }
)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "orden_id",nullable = false)
    private Order order;

    @Column(name = "proveedor",nullable = false)
    private String provider;

    @Column(name = "transaccion_id",nullable = false,unique = true)
    private String transactionId;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado",nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "creado_en",nullable = false,updatable = false,insertable = false)
    private OffsetDateTime createdAt;

    public void markAsApproved() {
        this.status = PaymentStatus.APPROVED;
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }

    public void markAsRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

}
