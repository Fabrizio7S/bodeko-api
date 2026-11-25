package com.reydiazz.bodeko.core.product.core.web.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record EditProductRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 200, message = "Name must have at most 200 characters")
        @jakarta.validation.constraints.Pattern(regexp="^[^\\x00]*$",message="El campo contiene un carácter nulo no permitido")
        String name,

        @Size(max = 10000, message = "La descripción admite hasta 10000 caracteres")
        @jakarta.validation.constraints.Pattern(regexp="^[^\\x00]*$",message="El campo contiene un carácter nulo no permitido")
        String description,

        @NotNull(message = "Base price is required")
        @DecimalMin(value = "0.01", message = "El precio debe ser al menos 0.01")
        @jakarta.validation.constraints.DecimalMax(value = "100000.00", message = "El precio máximo es 100000.00")
        @Digits(integer = 10, fraction = 2, message = "Base price must have at most 10 integer digits and 2 decimals")
        BigDecimal basePrice,

        @com.fasterxml.jackson.annotation.JsonAlias("imagePath")
        @Size(max = 500, message = "Image URL must have at most 500 characters")
        @jakarta.validation.constraints.Pattern(regexp="^[^\\x00]*$",message="El campo contiene un carácter nulo no permitido")
        String imageUrl,

        UUID categoryId,

        boolean taxExempt

) {
    /**
     * Acepta la ruta relativa devuelta por POST /products/images (se guarda tal cual) o,
     * por compatibilidad, una URL http/https sin credenciales. La pertenencia a la tienda
     * se verifica en el servicio.
     */
    @jakarta.validation.constraints.AssertTrue(message="La imagen debe ser una ruta subida o una URL http/https sin credenciales")
    public boolean isImageUrlValid() {
        if (imageUrl==null || imageUrl.isBlank()) return true;
        String value=imageUrl.trim();
        if (com.reydiazz.bodeko.shared.storage.ImageStorageService.isAbsoluteUrl(value)) {
            return com.reydiazz.bodeko.shared.storage.ImageStorageService.isValidExternalUrl(value);
        }
        return value.matches("^[A-Za-z0-9/_.-]+$") && !value.contains("..") && !value.startsWith("/");
    }
}
