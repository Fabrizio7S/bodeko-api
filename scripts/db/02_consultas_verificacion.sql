-- Consultas de SOLO LECTURA. Ejecutar después del seed y repetir tras usar la app.

-- 1. Usuarios de prueba. No muestra hashes de contraseña.
SELECT nombre_completo, correo, rol, activo
FROM usuarios WHERE id::text LIKE 'b0de0000-%' ORDER BY correo;

-- 2. Catálogo e inventario de ambas tiendas.
SELECT s.nombre AS tienda, p.nombre AS producto, p.estado, p.precio_base,
       p.exonerado_impuesto, i.cantidad, i.umbral_stock_bajo,
       i.cantidad <= i.umbral_stock_bajo AS alerta_stock_bajo
FROM productos p JOIN tiendas s ON s.id = p.tienda_id
JOIN inventario i ON i.producto_id = p.id
WHERE s.id IN ('b0de1000-0000-4000-8000-000000000001', 'b0de1000-0000-4000-8000-000000000002')
ORDER BY s.nombre, p.nombre;

-- 3. Ofertas y vigencia (la fecha cambia naturalmente con el paso del tiempo).
SELECT p.nombre, o.precio_oferta, o.inicia_en, o.termina_en,
       now() >= o.inicia_en AND now() < o.termina_en AS vigente
FROM ofertas_producto o JOIN productos p ON p.id = o.producto_id
WHERE p.id::text LIKE 'b0de3000-%' ORDER BY p.nombre;

-- 4. Carritos: confirma que permanecen tras cerrar e iniciar sesión.
SELECT u.correo, s.nombre AS tienda, c.estado, p.nombre AS producto, ci.cantidad
FROM carritos_compra c JOIN clientes u ON u.id = c.cliente_id JOIN tiendas s ON s.id = c.tienda_id
LEFT JOIN items_carrito_compra ci ON ci.carrito_id = c.id LEFT JOIN productos p ON p.id = ci.producto_id
WHERE u.id IN ('b0de0000-0000-4000-8000-000000000003', 'b0de0000-0000-4000-8000-000000000004')
ORDER BY c.creado_en DESC, p.nombre;

-- 5. Órdenes y pagos: incluye las órdenes nuevas creadas desde la aplicación.
SELECT o.numero_orden, o.estado AS estado_orden, o.tipo_comprobante,
       o.subtotal, o.monto_impuesto, o.total, pay.estado AS estado_pago, pay.monto
FROM ordenes o LEFT JOIN pagos pay ON pay.orden_id = o.id
WHERE o.tienda_id IN ('b0de1000-0000-4000-8000-000000000001', 'b0de1000-0000-4000-8000-000000000002')
ORDER BY o.creado_en DESC, pay.creado_en;

-- 6. Debe devolver CERO filas: subtotal e IGV cuadran con el total y las líneas.
SELECT o.numero_orden, o.subtotal, sum(oi.subtotal_item) AS subtotal_items, o.monto_impuesto, o.total
FROM ordenes o JOIN items_orden oi ON oi.orden_id = o.id
WHERE o.tienda_id IN ('b0de1000-0000-4000-8000-000000000001', 'b0de1000-0000-4000-8000-000000000002')
GROUP BY o.id
HAVING o.subtotal <> sum(oi.subtotal_item) OR o.total <> o.subtotal + o.monto_impuesto;

-- 7. Historial de cambios del usuario (administrador o cliente), incluidas tus pruebas nuevas.
SELECT o.numero_orden, h.estado_anterior, h.estado_nuevo,
       COALESCE(u.correo, 'No registrado (histórico)') AS usuario, h.creado_en
FROM historial_estado_orden h JOIN ordenes o ON o.id = h.orden_id LEFT JOIN usuarios u ON u.id = h.cambiado_por
WHERE o.tienda_id IN ('b0de1000-0000-4000-8000-000000000001', 'b0de1000-0000-4000-8000-000000000002')
ORDER BY h.creado_en DESC;
