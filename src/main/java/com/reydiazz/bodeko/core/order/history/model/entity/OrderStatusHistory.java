package com.reydiazz.bodeko.core.order.history.model.entity;

import com.reydiazz.bodeko.core.order.core.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Table(
        name = "historial_estado_orden",
        indexes = {
                @Index(
                        name = "idx_order_status_history_order_created",
                        columnList = "orden_id, creado_en"
                )
        }
)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderStatusHistory {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "orden_id", nullable = false, updatable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", nullable = false, updatable = false)
    private OrderStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, updatable = false)
    private OrderStatus toStatus;

    @Column(name = "cambiado_por", nullable = false, updatable = false)
    private UUID changedBy;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

}
