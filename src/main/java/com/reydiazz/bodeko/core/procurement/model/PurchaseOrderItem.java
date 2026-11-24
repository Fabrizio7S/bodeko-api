package com.reydiazz.bodeko.core.procurement.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

/** Variante y cantidades acordadas/recibidas en una orden de compra. */
@Entity
@Table(name = "items_orden_compra")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PurchaseOrderItem {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "orden_compra_id", nullable = false) private PurchaseOrder purchaseOrder;
    @Column(name = "variante_id", nullable = false, updatable = false) private UUID variantId;
    @Column(name = "nombre_producto", nullable = false, length = 200) private String productName;
    @Column(nullable = false, length = 80) private String sku;
    @Column(name = "opciones_variante", nullable = false, length = 500) private String variantOptions;
    @Column(name = "cantidad_pedida", nullable = false) private int quantityOrdered;
    @Builder.Default @Column(name = "cantidad_recibida", nullable = false) private int quantityReceived = 0;
    @Builder.Default @Column(name = "cantidad_rechazada", nullable = false) private int quantityRejected = 0;
    @Column(name = "costo_unitario", nullable = false, precision = 12, scale = 2) private BigDecimal unitCost;

    public int getPendingQuantity() { return quantityOrdered - quantityReceived - quantityRejected; }
    public void receive(int accepted, int rejected) { quantityReceived += accepted; quantityRejected += rejected; }
}
