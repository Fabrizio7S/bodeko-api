package com.reydiazz.bodeko.shared.storage;

/** Almacenamiento de objetos independiente del proveedor (hoy Cloudflare R2). */
public interface ObjectStorage {

    void put(String key, byte[] content, String contentType);

    void delete(String key);

}
