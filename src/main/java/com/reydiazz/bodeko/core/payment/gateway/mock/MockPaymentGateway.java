package com.reydiazz.bodeko.core.payment.gateway.mock;

import com.reydiazz.bodeko.core.payment.exception.PaymentErrorCode;
import com.reydiazz.bodeko.core.payment.exception.PaymentGatewayException;
import com.reydiazz.bodeko.core.payment.gateway.GatewayChargeRequest;
import com.reydiazz.bodeko.core.payment.gateway.GatewayChargeResult;
import com.reydiazz.bodeko.core.payment.gateway.GatewayRefundRequest;
import com.reydiazz.bodeko.core.payment.gateway.GatewayRefundResult;
import com.reydiazz.bodeko.core.payment.gateway.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Pasarela simulada para entornos académicos: no mueve dinero ni llama a servicios externos.
 * Se activa con PAYMENT_PROVIDER=mock. Usa los mismos tokens que el simulador del equipo:
 * tkn_test_ok aprueba y tkn_test_declined rechaza. Los cobros quedan registrados en la tabla
 * payments, por eso los reembolsos funcionan aunque el servidor se reinicie.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "payment.provider", havingValue = "mock")
public class MockPaymentGateway implements PaymentGateway {

    static final String APPROVED_TOKEN = "tkn_test_ok";
    static final String DECLINED_TOKEN = "tkn_test_declined";

    @Override
    public String provider() {
        return "MOCK";
    }

    @Override
    public GatewayChargeResult charge(GatewayChargeRequest request) {
        String token = request.sourceId() == null ? "" : request.sourceId().trim();
        String chargeId = "chr_mock_" + UUID.randomUUID().toString().replace("-", "");
        if (APPROVED_TOKEN.equals(token)) {
            log.info("Pago simulado aprobado para la orden {} ({} {})",
                    request.orderNumber(), request.amount(), request.currency());
            return new GatewayChargeResult(chargeId, true, "Pago simulado aprobado.");
        }
        if (DECLINED_TOKEN.equals(token)) {
            log.info("Pago simulado rechazado para la orden {}", request.orderNumber());
            return new GatewayChargeResult(chargeId, false,
                    "Pago rechazado (simulación): fondos insuficientes. Prueba con otro token.");
        }
        throw new PaymentGatewayException(
                "Token de prueba inválido. Usa " + APPROVED_TOKEN + " (aprobado) o " + DECLINED_TOKEN + " (rechazado).",
                PaymentErrorCode.GATEWAY_INVALID_REQUEST);
    }

    @Override
    public GatewayRefundResult refund(GatewayRefundRequest request) {
        // PaymentServiceImpl solo llama aquí si existe un pago APPROVED en la base de datos.
        String refundId = "ref_mock_" + UUID.randomUUID().toString().replace("-", "");
        log.info("Reembolso simulado {} de la orden {} ({})", refundId, request.orderNumber(), request.amount());
        return new GatewayRefundResult(refundId, true, null);
    }
}
