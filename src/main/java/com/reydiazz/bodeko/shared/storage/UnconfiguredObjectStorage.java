package com.reydiazz.bodeko.shared.storage;

import com.reydiazz.bodeko.shared.exception.BusinessException;

/** Se usa cuando faltan las variables R2_*: la API arranca, pero rechaza las subidas. */
public class UnconfiguredObjectStorage implements ObjectStorage {

    @Override
    public void put(String key, byte[] content, String contentType) {
        throw new BusinessException(StorageErrorCode.STORAGE_NOT_CONFIGURED);
    }

    @Override
    public void delete(String key) {
        // Nada que eliminar si no hay almacenamiento configurado.
    }
}
