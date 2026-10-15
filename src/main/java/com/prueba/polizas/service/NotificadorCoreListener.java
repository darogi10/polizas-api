package com.prueba.polizas.service;

import com.prueba.polizas.port.CoreGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Envía los eventos al CORE solo cuando la transacción de negocio confirma. */
@Component
public class NotificadorCoreListener {

    private final CoreGateway coreGateway;

    public NotificadorCoreListener(CoreGateway coreGateway) {
        this.coreGateway = coreGateway;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alConfirmar(EventoNegocio evento) {
        coreGateway.notificarEvento(evento.tipo().name(), evento.polizaId());
    }
}
