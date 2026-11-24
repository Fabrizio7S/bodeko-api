package com.reydiazz.bodeko.core.user.core.model.entity;

import com.reydiazz.bodeko.core.user.core.model.enums.UserDocumentType;
import com.reydiazz.bodeko.core.user.core.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Table(name = "usuarios")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "correo", nullable = false, unique = true)
    private String email;

    @Column(name = "hash_contrasena", nullable = false)
    private String password;

    @Column(name = "telefono" , length = 20)
    private String phoneNumber;

    /** Documento opcional del usuario. Cada orden conserva sus propios datos fiscales. */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", length = 20)
    private UserDocumentType documentType;

    @Column(name = "numero_documento", length = 20)
    private String documentNumber;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String fullName;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private UserRole role;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private boolean isActive = true;

    /** Suspende o reactiva la cuenta sin borrar su historial ni sus tiendas. */
    public void changeActive(boolean active) { this.isActive = active; }

}
