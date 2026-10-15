package com.prueba.polizas.port;

/** Puerto de salida hacia el CORE (sistema legado de pólizas). */
public interface CoreGateway {

    void notificarEvento(String evento, Long polizaId);
}
