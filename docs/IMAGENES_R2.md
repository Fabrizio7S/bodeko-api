# Subida de imágenes (Cloudflare R2)

## Configuración

Variables en `.env.dev` (ver `.env.example`):

| Variable | Uso |
| --- | --- |
| `R2_ACCOUNT_ID` | ID de la cuenta de Cloudflare. Arma el endpoint `https://<id>.r2.cloudflarestorage.com`. |
| `R2_ENDPOINT` | Opcional. Reemplaza el endpoint armado con el ID de la cuenta. |
| `R2_ACCESS_KEY_ID` / `R2_SECRET_ACCESS_KEY` | Token de API de R2 con permiso *Object Read & Write* sobre el bucket. |
| `R2_BUCKET` | Bucket donde se guardan las imágenes. |
| `R2_PUBLIC_URL` | Dominio público del bucket (propio o `https://pub-xxxx.r2.dev`). |

Si faltan, la API arranca igual y la subida responde `503 STORAGE_NOT_CONFIGURED`.

## Flujo

1. `POST /api/stores/{storeId}/products/images` (dueño de la tienda), `multipart/form-data`, campo `file`.
   - JPG, PNG, WEBP o GIF, máximo 5 MB. El tipo se detecta por el contenido, no por la extensión.
   - Respuesta `201`: `{ "path": "stores/<storeId>/products/<uuid>.png", "url": "https://.../stores/...", "contentType": "image/png", "size": 12345 }`
2. Enviar `path` en `imageUrl` (o `imagePath`) al crear o editar el producto.

## Qué se guarda

- En `productos.url_imagen` se guarda **solo la ruta relativa**. La URL pública se arma al responder con `R2_PUBLIC_URL`, así que cambiar de dominio o de proveedor no requiere migrar datos.
- Si el cliente reenvía la URL completa del propio bucket, se recorta a la ruta relativa.
- Una ruta solo se acepta si pertenece a la misma tienda (`stores/<storeId>/...`).
- Las URL externas `http/https` antiguas se siguen aceptando y se devuelven tal cual.
- Las respuestas de producto incluyen `imageUrl` (URL pública) e `imagePath` (ruta relativa, o `null` si es externa). Catálogo y carrito devuelven `imageUrl` ya resuelta.
- Al reemplazar la imagen de un producto, la anterior se elimina de R2 después de confirmar la transacción.

## Errores

`IMAGE_REQUIRED` (400), `IMAGE_PATH_INVALID` (400), `IMAGE_TOO_LARGE` (413), `IMAGE_UNSUPPORTED_TYPE` (415), `STORAGE_UNAVAILABLE` (502), `STORAGE_NOT_CONFIGURED` (503).
