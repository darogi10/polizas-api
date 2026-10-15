package com.prueba.polizas.dto;

import com.prueba.polizas.domain.EstadoPoliza;
import com.prueba.polizas.domain.Poliza;
import com.prueba.polizas.domain.TipoPoliza;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PolizaResponse(
        Long id,
        TipoPoliza tipo,
        EstadoPoliza estado,
        String tomador,
        BigDecimal canonMensual,
        BigDecimal prima,
        int mesesVigencia,
        LocalDate fechaInicio,
        LocalDate fechaFin) {

    public static PolizaResponse desde(Poliza poliza) {
        return new PolizaResponse(
                poliza.getId(),
                poliza.getTipo(),
                poliza.getEstado(),
                poliza.getTomador(),
                poliza.getCanonMensual(),
                poliza.getPrima(),
                poliza.getMesesVigencia(),
                poliza.getFechaInicio(),
                poliza.getFechaFin());
    }
}
