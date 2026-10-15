package com.prueba.polizas.service;

/** Evento de aplicación publicado por {@link PolizaService} cuando cambia el estado de una póliza o riesgo. */
public record EventoNegocio(TipoEventoCore tipo, Long polizaId) {
}
