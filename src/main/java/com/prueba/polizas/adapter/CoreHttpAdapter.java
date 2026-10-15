package com.prueba.polizas.adapter;

import com.prueba.polizas.port.CoreGateway;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Adapter HTTP del puerto {@link CoreGateway}.
 *
 * <p>Implementación "best effort": si el CORE no responde se registra un WARN y la
 * operación de negocio continúa. En producción esto iría por el patrón Outbox
 * (evento persistido en la misma transacción de negocio) + un publicador con
 * reintentos/backoff hacia el servicio agnóstico desplegado en WebLogic, para
 * garantizar entrega al menos una vez sin acoplar la disponibilidad del CORE.
 */
@Component
public class CoreHttpAdapter implements CoreGateway {

    private static final Logger log = LoggerFactory.getLogger(CoreHttpAdapter.class);

    private final RestClient restClient;
    private final String coreUrl;
    private final String apiKey;

    public CoreHttpAdapter(
            @Value("${core.url:http://localhost:8080/core-mock/evento}") String coreUrl,
            @Value("${core.api-key:${security.api-key:123456}}") String apiKey) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
        this.coreUrl = coreUrl;
        this.apiKey = apiKey;
    }

    @Override
    public void notificarEvento(String evento, Long polizaId) {
        try {
            restClient.post()
                    .uri(coreUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-api-key", apiKey)
                    .body(Map.of("evento", evento, "polizaId", polizaId))
                    .retrieve()
                    .toBodilessEntity();
            log.debug("Evento {} de póliza {} notificado al CORE", evento, polizaId);
        } catch (Exception e) {
            log.warn("No se pudo notificar el evento {} de la póliza {} al CORE ({}): {}",
                    evento, polizaId, coreUrl, e.getMessage());
        }
    }
}
