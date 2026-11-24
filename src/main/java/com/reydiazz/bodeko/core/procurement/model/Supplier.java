package com.reydiazz.bodeko.core.procurement.model;

import com.reydiazz.bodeko.core.store.model.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Proveedor registrado dentro de una tienda. */
@Entity
@Table(name = "proveedores")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Supplier {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "tienda_id", nullable = false) private Store store;
    @Column(name = "nombre", nullable = false, length = 120) private String name;
    @Column(name = "correo_contacto", length = 255) private String contactEmail;
    @Column(name = "telefono", length = 30) private String phone;
    @Column(name = "notas", length = 500) private String notes;
    @Builder.Default @Column(name = "activo", nullable = false) private boolean active = true;
    @Column(name = "creado_en", nullable = false, updatable = false) private OffsetDateTime createdAt;
}
