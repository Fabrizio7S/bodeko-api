package com.reydiazz.bodeko.core.superadmin.model;

import com.reydiazz.bodeko.core.user.core.model.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Registro de auditoría de las suspensiones y reactivaciones de cuentas. */
@Entity
@Table(name = "historial_estado_cuenta_plataforma")
@Getter @Builder @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlatformAccountStatusHistory {
    @Id @Column(nullable = false, updatable = false) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "actor_id", nullable = false, updatable = false) private User actor;
    @Enumerated(EnumType.STRING) @Column(name = "tipo_cuenta", nullable = false, updatable = false) private PlatformAccountType accountType;
    @Column(name = "cuenta_id", nullable = false, updatable = false) private UUID accountId;
    @Column(name = "activo_anterior", nullable = false, updatable = false) private boolean previousActive;
    @Column(name = "activo_nuevo", nullable = false, updatable = false) private boolean newActive;
    @Column(name = "motivo", nullable = false, length = 500, updatable = false) private String reason;
    @Column(name = "creado_en", nullable = false, updatable = false) private OffsetDateTime createdAt;
}
