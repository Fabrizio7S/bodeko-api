package com.reydiazz.bodeko.shared.idempotency;

import com.reydiazz.bodeko.shared.exception.BusinessException;
import com.reydiazz.bodeko.shared.validation.ValidationError;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.function.Supplier;

/** Guarda claves, huellas y resultados en PostgreSQL. Nunca almacena el token de pago recibido. */
@Service
public class IdempotencyService {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private final TransactionTemplate transaction;
    private final ThreadLocal<Identity> current = new ThreadLocal<>();
    private record Identity(UUID actor,String scope,String key) {}
    private record Fingerprint(String action,Object body) {}
    public IdempotencyService(JdbcTemplate jdbc, ObjectMapper mapper, PlatformTransactionManager manager) {
        this.jdbc=jdbc; this.mapper=mapper;
        this.transaction=new TransactionTemplate(manager);
        this.transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /** La respuesta y los efectos locales se confirman en la misma transacción. */
    public <T> T execute(UUID actor,String scope,String action,String key,Object body,Class<T> type,boolean external,Supplier<T> operation) {
        if (key==null || !key.matches("[A-Za-z0-9._:-]{8,128}")) throw new BusinessException(ValidationError.IDEMPOTENCY_REQUIRED);
        Identity identity=new Identity(actor,scope,key);
        String fingerprint=hash(mapper.writeValueAsString(new Fingerprint(action,body)));
        Map<String,Object> previous=transaction.execute(status -> claim(identity,fingerprint,external));
        if (previous!=null) {
            String state=(String)previous.get("estado");
            if ("COMPLETED".equals(state)) return mapper.readValue((String)previous.get("respuesta_json"),type);
            if ("FAILED".equals(state)) throw new StoredFailure(((Number)previous.get("estado_http")).intValue(),(String)previous.get("codigo_error"),(String)previous.get("mensaje_error"));
            throw new BusinessException("UNCERTAIN".equals(state)?ValidationError.OPERATION_UNCERTAIN:ValidationError.OPERATION_IN_PROGRESS);
        }
        current.set(identity);
        try {
            return transaction.execute(status -> {
                T response=operation.get();
                jdbc.update("update idempotencia_api set estado='COMPLETED',respuesta_json=?,actualizado_en=now() where actor_id=? and alcance=? and clave_solicitud=?",
                    mapper.writeValueAsString(response),actor,scope,key);
                return response;
            });
        } catch (RuntimeException error) {
            // Un fallo de red después de enviar dinero no autoriza un segundo intento.
            transaction.executeWithoutResult(status -> {
                Boolean started=jdbc.queryForObject("select externo_iniciado from idempotencia_api where actor_id=? and alcance=? and clave_solicitud=?",Boolean.class,actor,scope,key);
                boolean definite=error instanceof BusinessException b && !"PAYMENT_GATEWAY_UNAVAILABLE".equals(b.getError().getCode());
                boolean uncertain=Boolean.TRUE.equals(started) && !definite;
                int http=error instanceof BusinessException b?b.getError().getHttpStatus().value():503;
                String code=error instanceof BusinessException b?b.getError().getCode():"OPERATION_RETRY_REQUIRED";
                String message=error instanceof BusinessException b?Objects.toString(b.getMessage(),b.getError().getMessage()):"La operación no se completó. Reintenta con una nueva clave.";
                jdbc.update("update idempotencia_api set estado=?,estado_http=?,codigo_error=?,mensaje_error=?,actualizado_en=now() where actor_id=? and alcance=? and clave_solicitud=?",
                    uncertain?"UNCERTAIN":"FAILED",http,code,message.substring(0,Math.min(message.length(),1000)),actor,scope,key);
            });
            throw error;
        } finally { current.remove(); }
    }

    /** Se confirma antes de llamar al proveedor; sobrevive si cae el proceso después del envío. */
    public void markExternalStarted() {
        Identity id=current.get();
        if (id==null) throw new BusinessException(ValidationError.IDEMPOTENCY_REQUIRED);
        transaction.executeWithoutResult(status -> jdbc.update("update idempotencia_api set externo_iniciado=true,actualizado_en=now() where actor_id=? and alcance=? and clave_solicitud=?",id.actor(),id.scope(),id.key()));
    }

    private Map<String,Object> claim(Identity id,String fingerprint,boolean external) {
        // El cerrojo coordina reclamaciones del mismo recurso incluso con claves o actores distintos.
        jdbc.queryForList("select pg_advisory_xact_lock(hashtextextended(?,0))",id.scope());
        List<Map<String,Object>> existing=jdbc.queryForList("select * from idempotencia_api where actor_id=? and alcance=? and clave_solicitud=?",id.actor(),id.scope(),id.key());
        if (!existing.isEmpty()) {
            Map<String,Object> row=existing.getFirst();
            if (!fingerprint.equals(row.get("hash_solicitud"))) throw new BusinessException(ValidationError.IDEMPOTENCY_CONFLICT);
            return row;
        }
        if (external) {
            Integer blocked=jdbc.queryForObject("select count(*) from idempotencia_api where alcance=? and estado in ('IN_PROGRESS','UNCERTAIN')",Integer.class,id.scope());
            if (blocked!=null && blocked>0) throw new BusinessException(ValidationError.OPERATION_UNCERTAIN);
        }
        jdbc.update("insert into idempotencia_api(actor_id,alcance,clave_solicitud,hash_solicitud,estado) values(?,?,?,?,'IN_PROGRESS')",id.actor(),id.scope(),id.key(),fingerprint);
        return null;
    }

    private String hash(String text) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8))); }
        catch (java.security.NoSuchAlgorithmException error) { throw new IllegalStateException(error); }
    }
}
