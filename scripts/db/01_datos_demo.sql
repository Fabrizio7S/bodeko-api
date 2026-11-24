-- BODEKO: datos sintéticos para desarrollo, posteriores a Flyway V1-V16.
-- Ejecutar primero 00_compatibilidad_historial.sql y después este archivo completo.
-- Esta carga no crea/elimina tablas ni modifica datos existentes.
-- Contraseña de TODAS las cuentas demo: BodekoDemo2026!
-- El hash BCrypt está precalculado; NO requiere la extensión pgcrypto.
-- Los UUID son fijos. Reejecutar conserva cambios hechos desde la aplicación.
BEGIN;

DO $$
BEGIN
    IF to_regclass('public.idempotencia_api') IS NULL
       OR NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='ofertas_producto' AND column_name='porcentaje_descuento')
       OR to_regclass('public.historial_estado_orden') IS NULL
       OR to_regclass('public.clientes') IS NULL
       OR to_regclass('public.variantes_producto') IS NULL
       OR NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid='public.usuarios'::regclass AND conname='ck_users_role' AND pg_get_constraintdef(oid) LIKE '%SUPER_ADMIN%')
       OR NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='inventario' AND column_name='variante_id')
       OR NOT EXISTS (SELECT 1 FROM information_schema.columns
                      WHERE table_schema = 'public' AND table_name = 'usuarios' AND column_name = 'numero_documento') THEN
        RAISE EXCEPTION 'Primero inicia el backend para aplicar las migraciones Flyway V1-V16.';
    END IF;
END $$;

-- Detectar una BD antigua antes de insertar cualquier dato de prueba.
DO $$
BEGIN
    IF (SELECT count(*) FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'historial_estado_orden'
          AND column_name IN ('estado_anterior', 'estado_nuevo', 'cambiado_por', 'creado_en')) <> 4 THEN
        RAISE EXCEPTION 'La estructura del historial no coincide con el backend. Ejecuta primero scripts/db/00_compatibilidad_historial.sql.';
    END IF;
END $$;

-- Dos propietarios: permiten comprobar que cada uno solo administra sus propias tiendas.
INSERT INTO usuarios (id, correo, hash_contrasena, nombre_completo, rol, telefono, tipo_documento, numero_documento)
VALUES
('b0de0000-0000-4000-8000-000000000001', 'admin.demo@bodeko.test', '$2a$10$tlJgtV/apg1y/X5xe7Rju.oEx/hcwRcQSu8OQTtEE4pYA8YP6xy.m', 'Administrador Demo', 'ADMIN', '+51999999001', NULL, NULL),
('b0de0000-0000-4000-8000-000000000002', 'tecnologia.demo@bodeko.test', '$2a$10$tlJgtV/apg1y/X5xe7Rju.oEx/hcwRcQSu8OQTtEE4pYA8YP6xy.m', 'Administrador Tecnología', 'ADMIN', '+51999999002', NULL, NULL),
('b0de0000-0000-4000-8000-000000000006', 'superadmin.demo@bodeko.test', '$2a$10$tlJgtV/apg1y/X5xe7Rju.oEx/hcwRcQSu8OQTtEE4pYA8YP6xy.m', 'Superadministrador Demo', 'SUPER_ADMIN', NULL, NULL, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO tiendas (id, propietario_id, nombre, ruc, razon_social, subdominio, moneda, tasa_impuesto)
VALUES
('b0de1000-0000-4000-8000-000000000001', 'b0de0000-0000-4000-8000-000000000001', 'Bodeko Market Demo', '20999999001', 'Comercio Ficticio Demo S.A.C.', 'bodeko-market-demo', 'PEN', 0.18),
('b0de1000-0000-4000-8000-000000000002', 'b0de0000-0000-4000-8000-000000000002', 'Bodeko Tecnología Demo', '20999999002', 'Tecnología Ficticia Demo S.A.C.', 'bodeko-tech-demo', 'PEN', 0.18)
ON CONFLICT (id) DO NOTHING;

-- Los clientes pertenecen a una tienda (tabla customers). Dos clientes en Bodeko Market Demo
-- permiten comprobar que una cuenta no puede consultar las órdenes de otra; el primer correo
-- también existe en la tienda de tecnología como una cuenta independiente.
INSERT INTO clientes (id, tienda_id, correo, hash_contrasena, nombre_completo, telefono, tipo_documento, numero_documento)
VALUES
('b0de0000-0000-4000-8000-000000000003', 'b0de1000-0000-4000-8000-000000000001', 'cliente.demo@bodeko.test', '$2a$10$tlJgtV/apg1y/X5xe7Rju.oEx/hcwRcQSu8OQTtEE4pYA8YP6xy.m', 'Cliente Demo', '+51999999003', 'DNI', '12345678'),
('b0de0000-0000-4000-8000-000000000004', 'b0de1000-0000-4000-8000-000000000001', 'cliente2.demo@bodeko.test', '$2a$10$tlJgtV/apg1y/X5xe7Rju.oEx/hcwRcQSu8OQTtEE4pYA8YP6xy.m', 'Segundo Cliente Demo', '+51999999004', 'DNI', '87654321'),
('b0de0000-0000-4000-8000-000000000005', 'b0de1000-0000-4000-8000-000000000002', 'cliente.demo@bodeko.test', '$2a$10$tlJgtV/apg1y/X5xe7Rju.oEx/hcwRcQSu8OQTtEE4pYA8YP6xy.m', 'Cliente Demo', '+51999999003', NULL, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO categorias (id, tienda_id, nombre)
VALUES
('b0de2000-0000-4000-8000-000000000001', 'b0de1000-0000-4000-8000-000000000001', 'Abarrotes'),
('b0de2000-0000-4000-8000-000000000002', 'b0de1000-0000-4000-8000-000000000001', 'Bebidas y café'),
('b0de2000-0000-4000-8000-000000000003', 'b0de1000-0000-4000-8000-000000000001', 'Limpieza'),
('b0de2000-0000-4000-8000-000000000004', 'b0de1000-0000-4000-8000-000000000001', 'Categoría vacía de prueba'),
('b0de2000-0000-4000-8000-000000000005', 'b0de1000-0000-4000-8000-000000000002', 'Audio'),
('b0de2000-0000-4000-8000-000000000006', 'b0de1000-0000-4000-8000-000000000002', 'Periféricos')
ON CONFLICT (id) DO NOTHING;

-- Los datos ficticios no dependen de servicios externos de imágenes.
INSERT INTO productos (id, tienda_id, categoria_id, nombre, descripcion, precio_base, exonerado_impuesto, estado)
VALUES
('b0de3000-0000-4000-8000-000000000001', 'b0de1000-0000-4000-8000-000000000001', 'b0de2000-0000-4000-8000-000000000002', 'Café peruano 250 g', 'Producto demo con oferta activa y stock disponible.', 25.90, FALSE, 'ACTIVE'),
('b0de3000-0000-4000-8000-000000000002', 'b0de1000-0000-4000-8000-000000000001', 'b0de2000-0000-4000-8000-000000000001', 'Arroz extra 1 kg', 'Producto demo exonerado de IGV y con stock bajo.', 5.00, TRUE, 'ACTIVE'),
('b0de3000-0000-4000-8000-000000000003', 'b0de1000-0000-4000-8000-000000000001', 'b0de2000-0000-4000-8000-000000000001', 'Leche evaporada', 'Producto demo agotado para comprobar el filtro de stock.', 4.50, FALSE, 'ACTIVE'),
('b0de3000-0000-4000-8000-000000000004', 'b0de1000-0000-4000-8000-000000000001', 'b0de2000-0000-4000-8000-000000000001', 'Quinua 500 g', 'Producto demo exonerado de IGV.', 12.00, TRUE, 'ACTIVE'),
('b0de3000-0000-4000-8000-000000000005', 'b0de1000-0000-4000-8000-000000000001', 'b0de2000-0000-4000-8000-000000000003', 'Detergente 1 kg', 'Producto demo con una oferta ya vencida.', 18.00, FALSE, 'ACTIVE'),
('b0de3000-0000-4000-8000-000000000006', 'b0de1000-0000-4000-8000-000000000001', 'b0de2000-0000-4000-8000-000000000001', 'Chocolate en borrador', 'Solo visible para el administrador hasta su publicación.', 8.50, FALSE, 'DRAFT'),
('b0de3000-0000-4000-8000-000000000007', 'b0de1000-0000-4000-8000-000000000001', 'b0de2000-0000-4000-8000-000000000001', 'Azúcar archivada', 'Producto archivado que no debe aparecer en el catálogo público.', 4.20, FALSE, 'ARCHIVED'),
('b0de3000-0000-4000-8000-000000000008', 'b0de1000-0000-4000-8000-000000000001', 'b0de2000-0000-4000-8000-000000000001', 'Aceite de oliva 250 ml', 'Producto demo con una oferta programada para más adelante.', 22.00, FALSE, 'ACTIVE'),
('b0de3000-0000-4000-8000-000000000009', 'b0de1000-0000-4000-8000-000000000002', 'b0de2000-0000-4000-8000-000000000005', 'Audífonos inalámbricos', 'Producto de la segunda tienda para probar el aislamiento.', 89.00, FALSE, 'ACTIVE'),
('b0de3000-0000-4000-8000-000000000010', 'b0de1000-0000-4000-8000-000000000002', 'b0de2000-0000-4000-8000-000000000006', 'Teclado compacto', 'Producto demo con pocas unidades.', 129.00, FALSE, 'ACTIVE'),
('b0de3000-0000-4000-8000-000000000011', 'b0de1000-0000-4000-8000-000000000002', 'b0de2000-0000-4000-8000-000000000006', 'Mouse inalámbrico', 'Producto demo agotado.', 49.00, FALSE, 'ACTIVE'),
('b0de3000-0000-4000-8000-000000000012', 'b0de1000-0000-4000-8000-000000000002', 'b0de2000-0000-4000-8000-000000000006', 'Cámara web en borrador', 'Producto demo pendiente de publicación.', 159.00, FALSE, 'DRAFT')
ON CONFLICT (id) DO NOTHING;

-- La carga SQL crea las variantes directamente porque no pasa por ProductService.
INSERT INTO variantes_producto (id, producto_id, tienda_id, sku, opciones, clave_opciones, precio_base,
    predeterminada, activo, creado_en)
SELECT p.id, p.id, p.tienda_id,
    'BOD-' || upper(replace(p.id::text, '-', '')), '{}'::jsonb, 'DEFAULT', p.precio_base,
    TRUE, TRUE, p.creado_en
FROM productos p
WHERE p.id::text LIKE 'b0de3000-%'
ON CONFLICT (id) DO NOTHING;

-- El SQL no pasa por ProductService: por eso crea explícitamente los inventarios.
INSERT INTO inventario (id, producto_id, variante_id, cantidad, umbral_stock_bajo)
SELECT replace(producto_id, 'b0de3000', 'b0de4000')::uuid, producto_id::uuid, producto_id::uuid, cantidad, threshold
FROM (VALUES
('b0de3000-0000-4000-8000-000000000001', 40, 5),
('b0de3000-0000-4000-8000-000000000002', 4, 5),
('b0de3000-0000-4000-8000-000000000003', 0, 5),
('b0de3000-0000-4000-8000-000000000004', 25, 5),
('b0de3000-0000-4000-8000-000000000005', 12, 5),
('b0de3000-0000-4000-8000-000000000006', 0, 5),
('b0de3000-0000-4000-8000-000000000007', 0, 5),
('b0de3000-0000-4000-8000-000000000008', 20, 5),
('b0de3000-0000-4000-8000-000000000009', 8, 3),
('b0de3000-0000-4000-8000-000000000010', 2, 3),
('b0de3000-0000-4000-8000-000000000011', 0, 3),
('b0de3000-0000-4000-8000-000000000012', 0, 3)
) AS stock(producto_id, cantidad, threshold)
ON CONFLICT (variante_id) DO NOTHING;

-- Se insertan solo si el producto aún no tiene ofertas: repetir no crea solapamientos.
INSERT INTO ofertas_producto (tienda_id, producto_id, precio_oferta, inicia_en, termina_en, porcentaje_descuento)
SELECT p.tienda_id, p.id, offers.price, now() + offers.starts_offset, now() + offers.ends_offset,
       CASE WHEN p.precio_base>0 AND round(100*(1-offers.price/p.precio_base),2) BETWEEN 0 AND 99.99
          AND round(p.precio_base*(1-round(100*(1-offers.price/p.precio_base),2)/100),2)=offers.price
       THEN round(100*(1-offers.price/p.precio_base),2) ELSE NULL END
FROM (VALUES
('b0de3000-0000-4000-8000-000000000001'::uuid, 19.90, interval '-1 day', interval '30 days'),
('b0de3000-0000-4000-8000-000000000005'::uuid, 14.00, interval '-10 days', interval '-1 day'),
('b0de3000-0000-4000-8000-000000000008'::uuid, 18.00, interval '7 days', interval '14 days'),
('b0de3000-0000-4000-8000-000000000009'::uuid, 69.00, interval '-1 day', interval '30 days')
) AS offers(producto_id, price, starts_offset, ends_offset)
JOIN productos p ON p.id = offers.producto_id
WHERE NOT EXISTS (SELECT 1 FROM ofertas_producto existing WHERE existing.producto_id = p.id);

INSERT INTO carritos_compra (id, cliente_id, tienda_id, estado)
VALUES ('b0de6000-0000-4000-8000-000000000001', 'b0de0000-0000-4000-8000-000000000003', 'b0de1000-0000-4000-8000-000000000001', 'OPEN')
ON CONFLICT DO NOTHING;

INSERT INTO items_carrito_compra (tienda_id, carrito_id, producto_id, variante_id, cantidad)
SELECT c.tienda_id, c.id, items.producto_id, items.producto_id, items.cantidad
FROM carritos_compra c
CROSS JOIN (VALUES
('b0de3000-0000-4000-8000-000000000001'::uuid, 1),
('b0de3000-0000-4000-8000-000000000002'::uuid, 2)
) AS items(producto_id, cantidad)
WHERE c.id = 'b0de6000-0000-4000-8000-000000000001' AND c.estado = 'OPEN'
ON CONFLICT (carrito_id, variante_id) DO NOTHING;

-- Historial ficticio ya contabilizado en el stock inicial; no son operaciones reales.
-- Todas las líneas usan un café a S/25.90, IGV S/4.66, total S/30.56.
INSERT INTO ordenes (id, tienda_id, cliente_id, numero_orden, tipo_comprobante,
    cliente_tipo_documento, cliente_numero_documento, cliente_razon_social,
    cliente_direccion_facturacion, subtotal, monto_impuesto, total, tasa_impuesto_aplicada, estado, creado_en)
SELECT fixture.id, 'b0de1000-0000-4000-8000-000000000001', 'b0de0000-0000-4000-8000-000000000003',
    fixture.number, fixture.invoice, CASE WHEN fixture.invoice = 'INVOICE' THEN 'RUC' ELSE 'DNI' END,
    CASE WHEN fixture.invoice = 'INVOICE' THEN '20999999003' ELSE '12345678' END,
    CASE WHEN fixture.invoice = 'INVOICE' THEN 'Cliente Ficticio Demo S.A.C.' ELSE NULL END,
    'Dirección ficticia de prueba 123', 25.90, 4.66, 30.56, 0.18, fixture.estado, now() - fixture.age
FROM (VALUES
('b0de7000-0000-4000-8000-000000000001'::uuid, 'BOD-DEMO-PENDIENTE', 'RECEIPT', 'PENDING', interval '2 hours'),
('b0de7000-0000-4000-8000-000000000002'::uuid, 'BOD-DEMO-PAGADA', 'INVOICE', 'PAID', interval '1 day'),
('b0de7000-0000-4000-8000-000000000003'::uuid, 'BOD-DEMO-ENVIADA', 'RECEIPT', 'SHIPPED', interval '2 days'),
('b0de7000-0000-4000-8000-000000000004'::uuid, 'BOD-DEMO-ENTREGADA', 'INVOICE', 'DELIVERED', interval '3 days'),
('b0de7000-0000-4000-8000-000000000005'::uuid, 'BOD-DEMO-CANCELADA', 'RECEIPT', 'CANCELLED', interval '4 days')
) AS fixture(id, number, invoice, estado, age)
ON CONFLICT (id) DO NOTHING;

INSERT INTO items_orden (tienda_id, orden_id, producto_id, variante_id, sku_variante, opciones_variante,
    nombre_producto, cantidad, precio_unitario_lista, precio_unitario_cobrado, subtotal_item)
SELECT o.tienda_id, o.id, p.id, p.id, pv.sku, pv.opciones, p.nombre, 1, 25.90, 25.90, 25.90
FROM ordenes o
JOIN productos p ON p.id = 'b0de3000-0000-4000-8000-000000000001'
JOIN variantes_producto pv ON pv.id = p.id
WHERE o.id IN (
'b0de7000-0000-4000-8000-000000000001', 'b0de7000-0000-4000-8000-000000000002',
'b0de7000-0000-4000-8000-000000000003', 'b0de7000-0000-4000-8000-000000000004',
'b0de7000-0000-4000-8000-000000000005')
ON CONFLICT (orden_id, variante_id) DO NOTHING;

INSERT INTO pagos (id, orden_id, proveedor, transaccion_id, monto, estado)
VALUES
('b0de8000-0000-4000-8000-000000000001', 'b0de7000-0000-4000-8000-000000000001', 'CULQI', 'chr_test_seed_declined', 30.56, 'FAILED'),
('b0de8000-0000-4000-8000-000000000002', 'b0de7000-0000-4000-8000-000000000002', 'CULQI', 'chr_test_seed_paid', 30.56, 'APPROVED'),
('b0de8000-0000-4000-8000-000000000003', 'b0de7000-0000-4000-8000-000000000003', 'CULQI', 'chr_test_seed_shipped', 30.56, 'APPROVED'),
('b0de8000-0000-4000-8000-000000000004', 'b0de7000-0000-4000-8000-000000000004', 'CULQI', 'chr_test_seed_delivered', 30.56, 'APPROVED'),
('b0de8000-0000-4000-8000-000000000005', 'b0de7000-0000-4000-8000-000000000005', 'CULQI', 'chr_test_seed_refunded', 30.56, 'REFUNDED')
ON CONFLICT (id) DO NOTHING;

-- Las transiciones de preparación/envío/cancelación que administra el backend.
INSERT INTO historial_estado_orden (id, orden_id, estado_anterior, estado_nuevo, cambiado_por, creado_en)
VALUES
('b0de9000-0000-4000-8000-000000000001', 'b0de7000-0000-4000-8000-000000000003', 'PAID', 'SHIPPED', 'b0de0000-0000-4000-8000-000000000001', now() - interval '1 day'),
('b0de9000-0000-4000-8000-000000000002', 'b0de7000-0000-4000-8000-000000000004', 'PAID', 'SHIPPED', 'b0de0000-0000-4000-8000-000000000001', now() - interval '2 days'),
('b0de9000-0000-4000-8000-000000000003', 'b0de7000-0000-4000-8000-000000000004', 'SHIPPED', 'DELIVERED', 'b0de0000-0000-4000-8000-000000000001', now() - interval '1 day'),
('b0de9000-0000-4000-8000-000000000004', 'b0de7000-0000-4000-8000-000000000005', 'PAID', 'CANCELLED', 'b0de0000-0000-4000-8000-000000000001', now() - interval '3 days')
ON CONFLICT (id) DO NOTHING;

COMMIT;
