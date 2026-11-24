-- Traduce al español los nombres de tablas y columnas.
-- Solo cambia identificadores: los datos y los valores de estado ('PENDING', 'ADMIN', ...) no se modifican.
-- PostgreSQL actualiza automáticamente índices, restricciones CHECK y claves foráneas que usan estas columnas.
-- Los nombres de restricciones e índices se conservan porque el backend los usa para reconocer duplicados.

-- users -> usuarios
ALTER TABLE users RENAME COLUMN email TO correo;
ALTER TABLE users RENAME COLUMN password_hash TO hash_contrasena;
ALTER TABLE users RENAME COLUMN full_name TO nombre_completo;
ALTER TABLE users RENAME COLUMN role TO rol;
ALTER TABLE users RENAME COLUMN created_at TO creado_en;
ALTER TABLE users RENAME COLUMN is_active TO activo;
ALTER TABLE users RENAME COLUMN phone_number TO telefono;
ALTER TABLE users RENAME COLUMN document_type TO tipo_documento;
ALTER TABLE users RENAME COLUMN document_number TO numero_documento;
ALTER TABLE users RENAME TO usuarios;

-- stores -> tiendas
ALTER TABLE stores RENAME COLUMN owner_id TO propietario_id;
ALTER TABLE stores RENAME COLUMN name TO nombre;
ALTER TABLE stores RENAME COLUMN tax_id TO ruc;
ALTER TABLE stores RENAME COLUMN legal_name TO razon_social;
ALTER TABLE stores RENAME COLUMN subdomain TO subdominio;
ALTER TABLE stores RENAME COLUMN currency TO moneda;
ALTER TABLE stores RENAME COLUMN tax_rate TO tasa_impuesto;
ALTER TABLE stores RENAME COLUMN created_at TO creado_en;
ALTER TABLE stores RENAME COLUMN billing_issuer_id TO emisor_facturacion_id;
ALTER TABLE stores RENAME TO tiendas;

-- categories -> categorias
ALTER TABLE categories RENAME COLUMN store_id TO tienda_id;
ALTER TABLE categories RENAME COLUMN name TO nombre;
ALTER TABLE categories RENAME TO categorias;

-- products -> productos
ALTER TABLE products RENAME COLUMN store_id TO tienda_id;
ALTER TABLE products RENAME COLUMN category_id TO categoria_id;
ALTER TABLE products RENAME COLUMN name TO nombre;
ALTER TABLE products RENAME COLUMN description TO descripcion;
ALTER TABLE products RENAME COLUMN base_price TO precio_base;
ALTER TABLE products RENAME COLUMN image_url TO url_imagen;
ALTER TABLE products RENAME COLUMN is_tax_exempt TO exonerado_impuesto;
ALTER TABLE products RENAME COLUMN status TO estado;
ALTER TABLE products RENAME COLUMN created_at TO creado_en;
ALTER TABLE products RENAME TO productos;

-- inventory -> inventario
ALTER TABLE inventory RENAME COLUMN product_id TO producto_id;
ALTER TABLE inventory RENAME COLUMN quantity TO cantidad;
ALTER TABLE inventory RENAME COLUMN low_stock_threshold TO umbral_stock_bajo;
ALTER TABLE inventory RENAME COLUMN updated_at TO actualizado_en;
ALTER TABLE inventory RENAME COLUMN variant_id TO variante_id;
ALTER TABLE inventory RENAME TO inventario;

-- product_offers -> ofertas_producto
ALTER TABLE product_offers RENAME COLUMN store_id TO tienda_id;
ALTER TABLE product_offers RENAME COLUMN product_id TO producto_id;
ALTER TABLE product_offers RENAME COLUMN offer_price TO precio_oferta;
ALTER TABLE product_offers RENAME COLUMN starts_at TO inicia_en;
ALTER TABLE product_offers RENAME COLUMN ends_at TO termina_en;
ALTER TABLE product_offers RENAME COLUMN created_at TO creado_en;
ALTER TABLE product_offers RENAME COLUMN discount_percentage TO porcentaje_descuento;
ALTER TABLE product_offers RENAME TO ofertas_producto;

-- shopping_carts -> carritos_compra
ALTER TABLE shopping_carts RENAME COLUMN user_id TO cliente_id;
ALTER TABLE shopping_carts RENAME COLUMN store_id TO tienda_id;
ALTER TABLE shopping_carts RENAME COLUMN status TO estado;
ALTER TABLE shopping_carts RENAME COLUMN created_at TO creado_en;
ALTER TABLE shopping_carts RENAME COLUMN updated_at TO actualizado_en;
ALTER TABLE shopping_carts RENAME COLUMN recovery_email_sent_at TO correo_recuperacion_enviado_en;
ALTER TABLE shopping_carts RENAME TO carritos_compra;

-- shopping_cart_items -> items_carrito_compra
ALTER TABLE shopping_cart_items RENAME COLUMN store_id TO tienda_id;
ALTER TABLE shopping_cart_items RENAME COLUMN cart_id TO carrito_id;
ALTER TABLE shopping_cart_items RENAME COLUMN product_id TO producto_id;
ALTER TABLE shopping_cart_items RENAME COLUMN quantity TO cantidad;
ALTER TABLE shopping_cart_items RENAME COLUMN variant_id TO variante_id;
ALTER TABLE shopping_cart_items RENAME TO items_carrito_compra;

-- orders -> ordenes
ALTER TABLE orders RENAME COLUMN store_id TO tienda_id;
ALTER TABLE orders RENAME COLUMN customer_id TO cliente_id;
ALTER TABLE orders RENAME COLUMN order_number TO numero_orden;
ALTER TABLE orders RENAME COLUMN invoice_type TO tipo_comprobante;
ALTER TABLE orders RENAME COLUMN customer_document_type TO cliente_tipo_documento;
ALTER TABLE orders RENAME COLUMN customer_document_number TO cliente_numero_documento;
ALTER TABLE orders RENAME COLUMN customer_business_name TO cliente_razon_social;
ALTER TABLE orders RENAME COLUMN customer_billing_address TO cliente_direccion_facturacion;
ALTER TABLE orders RENAME COLUMN tax_amount TO monto_impuesto;
ALTER TABLE orders RENAME COLUMN applied_tax_rate TO tasa_impuesto_aplicada;
ALTER TABLE orders RENAME COLUMN status TO estado;
ALTER TABLE orders RENAME COLUMN created_at TO creado_en;
ALTER TABLE orders RENAME COLUMN tracking_carrier TO transportista;
ALTER TABLE orders RENAME COLUMN tracking_number TO numero_seguimiento;
ALTER TABLE orders RENAME COLUMN tracking_url TO url_seguimiento;
ALTER TABLE orders RENAME COLUMN estimated_delivery_at TO entrega_estimada_en;
ALTER TABLE orders RENAME TO ordenes;

-- order_items -> items_orden
ALTER TABLE order_items RENAME COLUMN store_id TO tienda_id;
ALTER TABLE order_items RENAME COLUMN order_id TO orden_id;
ALTER TABLE order_items RENAME COLUMN product_id TO producto_id;
ALTER TABLE order_items RENAME COLUMN product_name TO nombre_producto;
ALTER TABLE order_items RENAME COLUMN quantity TO cantidad;
ALTER TABLE order_items RENAME COLUMN list_unit_price TO precio_unitario_lista;
ALTER TABLE order_items RENAME COLUMN charged_unit_price TO precio_unitario_cobrado;
ALTER TABLE order_items RENAME COLUMN item_subtotal TO subtotal_item;
ALTER TABLE order_items RENAME COLUMN variant_id TO variante_id;
ALTER TABLE order_items RENAME COLUMN variant_sku TO sku_variante;
ALTER TABLE order_items RENAME COLUMN variant_options TO opciones_variante;
ALTER TABLE order_items RENAME TO items_orden;

-- payments -> pagos
ALTER TABLE payments RENAME COLUMN order_id TO orden_id;
ALTER TABLE payments RENAME COLUMN provider TO proveedor;
ALTER TABLE payments RENAME COLUMN transaction_id TO transaccion_id;
ALTER TABLE payments RENAME COLUMN amount TO monto;
ALTER TABLE payments RENAME COLUMN status TO estado;
ALTER TABLE payments RENAME COLUMN created_at TO creado_en;
ALTER TABLE payments RENAME TO pagos;

-- order_status_history -> historial_estado_orden
ALTER TABLE order_status_history RENAME COLUMN order_id TO orden_id;
ALTER TABLE order_status_history RENAME COLUMN from_status TO estado_anterior;
ALTER TABLE order_status_history RENAME COLUMN to_status TO estado_nuevo;
ALTER TABLE order_status_history RENAME COLUMN changed_by TO cambiado_por;
ALTER TABLE order_status_history RENAME COLUMN created_at TO creado_en;
ALTER TABLE order_status_history RENAME TO historial_estado_orden;

-- api_idempotency -> idempotencia_api
ALTER TABLE api_idempotency RENAME COLUMN scope TO alcance;
ALTER TABLE api_idempotency RENAME COLUMN request_key TO clave_solicitud;
ALTER TABLE api_idempotency RENAME COLUMN request_hash TO hash_solicitud;
ALTER TABLE api_idempotency RENAME COLUMN state TO estado;
ALTER TABLE api_idempotency RENAME COLUMN external_started TO externo_iniciado;
ALTER TABLE api_idempotency RENAME COLUMN response_json TO respuesta_json;
ALTER TABLE api_idempotency RENAME COLUMN http_status TO estado_http;
ALTER TABLE api_idempotency RENAME COLUMN error_code TO codigo_error;
ALTER TABLE api_idempotency RENAME COLUMN error_message TO mensaje_error;
ALTER TABLE api_idempotency RENAME COLUMN created_at TO creado_en;
ALTER TABLE api_idempotency RENAME COLUMN updated_at TO actualizado_en;
ALTER TABLE api_idempotency RENAME TO idempotencia_api;

-- customers -> clientes
ALTER TABLE customers RENAME COLUMN store_id TO tienda_id;
ALTER TABLE customers RENAME COLUMN email TO correo;
ALTER TABLE customers RENAME COLUMN password_hash TO hash_contrasena;
ALTER TABLE customers RENAME COLUMN full_name TO nombre_completo;
ALTER TABLE customers RENAME COLUMN phone_number TO telefono;
ALTER TABLE customers RENAME COLUMN document_type TO tipo_documento;
ALTER TABLE customers RENAME COLUMN document_number TO numero_documento;
ALTER TABLE customers RENAME COLUMN created_at TO creado_en;
ALTER TABLE customers RENAME COLUMN is_active TO activo;
ALTER TABLE customers RENAME COLUMN marketing_consent TO consentimiento_marketing;
ALTER TABLE customers RENAME TO clientes;

-- product_variants -> variantes_producto
ALTER TABLE product_variants RENAME COLUMN product_id TO producto_id;
ALTER TABLE product_variants RENAME COLUMN store_id TO tienda_id;
ALTER TABLE product_variants RENAME COLUMN options TO opciones;
ALTER TABLE product_variants RENAME COLUMN options_key TO clave_opciones;
ALTER TABLE product_variants RENAME COLUMN base_price TO precio_base;
ALTER TABLE product_variants RENAME COLUMN is_default TO predeterminada;
ALTER TABLE product_variants RENAME COLUMN is_active TO activo;
ALTER TABLE product_variants RENAME COLUMN created_at TO creado_en;
ALTER TABLE product_variants RENAME TO variantes_producto;

-- platform_account_status_history -> historial_estado_cuenta_plataforma
ALTER TABLE platform_account_status_history RENAME COLUMN account_type TO tipo_cuenta;
ALTER TABLE platform_account_status_history RENAME COLUMN account_id TO cuenta_id;
ALTER TABLE platform_account_status_history RENAME COLUMN previous_active TO activo_anterior;
ALTER TABLE platform_account_status_history RENAME COLUMN new_active TO activo_nuevo;
ALTER TABLE platform_account_status_history RENAME COLUMN reason TO motivo;
ALTER TABLE platform_account_status_history RENAME COLUMN created_at TO creado_en;
ALTER TABLE platform_account_status_history RENAME TO historial_estado_cuenta_plataforma;

-- inventory_movements -> movimientos_inventario
ALTER TABLE inventory_movements RENAME COLUMN store_id TO tienda_id;
ALTER TABLE inventory_movements RENAME COLUMN product_id TO producto_id;
ALTER TABLE inventory_movements RENAME COLUMN variant_id TO variante_id;
ALTER TABLE inventory_movements RENAME COLUMN product_name TO nombre_producto;
ALTER TABLE inventory_movements RENAME COLUMN variant_sku TO sku_variante;
ALTER TABLE inventory_movements RENAME COLUMN variant_options TO opciones_variante;
ALTER TABLE inventory_movements RENAME COLUMN movement_type TO tipo_movimiento;
ALTER TABLE inventory_movements RENAME COLUMN units TO unidades;
ALTER TABLE inventory_movements RENAME COLUMN quantity_change TO cambio_cantidad;
ALTER TABLE inventory_movements RENAME COLUMN quantity_before TO cantidad_anterior;
ALTER TABLE inventory_movements RENAME COLUMN quantity_after TO cantidad_posterior;
ALTER TABLE inventory_movements RENAME COLUMN reference_type TO tipo_referencia;
ALTER TABLE inventory_movements RENAME COLUMN reference_id TO referencia_id;
ALTER TABLE inventory_movements RENAME COLUMN reference_number TO numero_referencia;
ALTER TABLE inventory_movements RENAME COLUMN reason TO motivo;
ALTER TABLE inventory_movements RENAME COLUMN actor_name TO actor_nombre;
ALTER TABLE inventory_movements RENAME COLUMN actor_email TO actor_correo;
ALTER TABLE inventory_movements RENAME COLUMN actor_role TO actor_rol;
ALTER TABLE inventory_movements RENAME COLUMN created_at TO creado_en;
ALTER TABLE inventory_movements RENAME TO movimientos_inventario;

-- billing_issuers -> emisores_facturacion
ALTER TABLE billing_issuers RENAME COLUMN owner_id TO propietario_id;
ALTER TABLE billing_issuers RENAME COLUMN tax_id TO ruc;
ALTER TABLE billing_issuers RENAME COLUMN legal_name TO razon_social;
ALTER TABLE billing_issuers RENAME COLUMN fiscal_address TO direccion_fiscal;
ALTER TABLE billing_issuers RENAME COLUMN created_at TO creado_en;
ALTER TABLE billing_issuers RENAME COLUMN updated_at TO actualizado_en;
ALTER TABLE billing_issuers RENAME TO emisores_facturacion;

-- billing_document_sequences -> secuencias_documento_facturacion
ALTER TABLE billing_document_sequences RENAME COLUMN issuer_id TO emisor_id;
ALTER TABLE billing_document_sequences RENAME COLUMN invoice_type TO tipo_comprobante;
ALTER TABLE billing_document_sequences RENAME COLUMN next_number TO siguiente_numero;
ALTER TABLE billing_document_sequences RENAME TO secuencias_documento_facturacion;

-- sales_documents -> documentos_venta
ALTER TABLE sales_documents RENAME COLUMN order_id TO orden_id;
ALTER TABLE sales_documents RENAME COLUMN store_id TO tienda_id;
ALTER TABLE sales_documents RENAME COLUMN billing_issuer_id TO emisor_facturacion_id;
ALTER TABLE sales_documents RENAME COLUMN invoice_type TO tipo_comprobante;
ALTER TABLE sales_documents RENAME COLUMN status TO estado;
ALTER TABLE sales_documents RENAME COLUMN series TO serie;
ALTER TABLE sales_documents RENAME COLUMN document_number TO numero_documento;
ALTER TABLE sales_documents RENAME COLUMN issuer_tax_id TO emisor_ruc;
ALTER TABLE sales_documents RENAME COLUMN issuer_legal_name TO emisor_razon_social;
ALTER TABLE sales_documents RENAME COLUMN issuer_fiscal_address TO emisor_direccion_fiscal;
ALTER TABLE sales_documents RENAME COLUMN issued_at TO emitido_en;
ALTER TABLE sales_documents RENAME COLUMN created_at TO creado_en;
ALTER TABLE sales_documents RENAME TO documentos_venta;

-- suppliers -> proveedores
ALTER TABLE suppliers RENAME COLUMN store_id TO tienda_id;
ALTER TABLE suppliers RENAME COLUMN name TO nombre;
ALTER TABLE suppliers RENAME COLUMN contact_email TO correo_contacto;
ALTER TABLE suppliers RENAME COLUMN phone TO telefono;
ALTER TABLE suppliers RENAME COLUMN notes TO notas;
ALTER TABLE suppliers RENAME COLUMN active TO activo;
ALTER TABLE suppliers RENAME COLUMN created_at TO creado_en;
ALTER TABLE suppliers RENAME TO proveedores;

-- purchase_orders -> ordenes_compra
ALTER TABLE purchase_orders RENAME COLUMN store_id TO tienda_id;
ALTER TABLE purchase_orders RENAME COLUMN supplier_id TO proveedor_id;
ALTER TABLE purchase_orders RENAME COLUMN order_number TO numero_orden;
ALTER TABLE purchase_orders RENAME COLUMN status TO estado;
ALTER TABLE purchase_orders RENAME COLUMN notes TO notas;
ALTER TABLE purchase_orders RENAME COLUMN created_at TO creado_en;
ALTER TABLE purchase_orders RENAME TO ordenes_compra;

-- purchase_order_items -> items_orden_compra
ALTER TABLE purchase_order_items RENAME COLUMN purchase_order_id TO orden_compra_id;
ALTER TABLE purchase_order_items RENAME COLUMN variant_id TO variante_id;
ALTER TABLE purchase_order_items RENAME COLUMN product_name TO nombre_producto;
ALTER TABLE purchase_order_items RENAME COLUMN variant_options TO opciones_variante;
ALTER TABLE purchase_order_items RENAME COLUMN quantity_ordered TO cantidad_pedida;
ALTER TABLE purchase_order_items RENAME COLUMN quantity_received TO cantidad_recibida;
ALTER TABLE purchase_order_items RENAME COLUMN quantity_rejected TO cantidad_rechazada;
ALTER TABLE purchase_order_items RENAME COLUMN unit_cost TO costo_unitario;
ALTER TABLE purchase_order_items RENAME TO items_orden_compra;
