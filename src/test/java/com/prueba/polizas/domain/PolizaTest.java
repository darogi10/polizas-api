package com.prueba.polizas.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.prueba.polizas.exception.ReglaNegocioException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PolizaTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 1, 1);

    private static Poliza nuevaPoliza(TipoPoliza tipo) {
        return new Poliza(tipo, "Tomador de prueba", new BigDecimal("1000000"), 12, INICIO);
    }

    @Test
    void renovarPolizaCanceladaFalla() {
        Poliza poliza = nuevaPoliza(TipoPoliza.INDIVIDUAL);
        poliza.cancelar();

        assertThatThrownBy(() -> poliza.renovar(new BigDecimal("5.2")))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("cancelada");
        assertThat(poliza.getEstado()).isEqualTo(EstadoPoliza.CANCELADA);
    }

    @Test
    void renovarAjustaCanonPorIpcYRecalculaPrima() {
        Poliza poliza = nuevaPoliza(TipoPoliza.INDIVIDUAL);
        assertThat(poliza.getPrima()).isEqualByComparingTo("12000000.00");

        poliza.renovar(new BigDecimal("5.2"));

        assertThat(poliza.getCanonMensual()).isEqualByComparingTo("1052000.00");
        assertThat(poliza.getPrima()).isEqualByComparingTo("12624000.00");
        assertThat(poliza.getFechaInicio()).isEqualTo(LocalDate.of(2027, 1, 1));
        assertThat(poliza.getFechaFin()).isEqualTo(LocalDate.of(2028, 1, 1));
        assertThat(poliza.getEstado()).isEqualTo(EstadoPoliza.RENOVADA);
    }

    @Test
    void cancelarPolizaCancelaTodosSusRiesgos() {
        Poliza poliza = nuevaPoliza(TipoPoliza.COLECTIVA);
        poliza.agregarRiesgo("Calle 1 # 2-3", "Arrendatario A");
        Riesgo yaCancelado = poliza.agregarRiesgo("Calle 4 # 5-6", "Arrendatario B");
        poliza.agregarRiesgo("Calle 7 # 8-9", "Arrendatario C");
        yaCancelado.cancelar();

        poliza.cancelar();

        assertThat(poliza.getEstado()).isEqualTo(EstadoPoliza.CANCELADA);
        assertThat(poliza.getRiesgos())
                .hasSize(3)
                .allSatisfy(r -> assertThat(r.getEstado()).isEqualTo(EstadoRiesgo.CANCELADO));
    }

    @Test
    void agregarRiesgoEnPolizaIndividualFalla() {
        Poliza poliza = nuevaPoliza(TipoPoliza.INDIVIDUAL);

        assertThatThrownBy(() -> poliza.agregarRiesgo("Calle 1 # 2-3", "Arrendatario"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("colectivas");
        assertThat(poliza.getRiesgos()).isEmpty();
    }
}
