package com.prueba.polizas.dto;

import com.prueba.polizas.domain.EstadoRiesgo;
import com.prueba.polizas.domain.Riesgo;

public record RiesgoResponse(
        Long id,
        Long polizaId,
        String direccionInmueble,
        String arrendatario,
        EstadoRiesgo estado) {

    public static RiesgoResponse desde(Riesgo riesgo) {
        return new RiesgoResponse(
                riesgo.getId(),
                riesgo.getPoliza().getId(),
                riesgo.getDireccionInmueble(),
                riesgo.getArrendatario(),
                riesgo.getEstado());
    }
}
