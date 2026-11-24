package com.reydiazz.bodeko.core.category.model;

import com.reydiazz.bodeko.core.store.model.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Getter
@Table(
        name = "categorias",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_categories_store_name",
                        columnNames = {"tienda_id", "nombre"}
                ),
                @UniqueConstraint(
                        name = "uq_categories_id_store",
                        columnNames = {"id", "tienda_id"}
                )
        }
)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Category {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tienda_id", nullable = false)
    private Store store;

    @Column(name = "nombre",nullable = false)
    private String name;

    public void update(String name){
        this.name = name;
    }

}
