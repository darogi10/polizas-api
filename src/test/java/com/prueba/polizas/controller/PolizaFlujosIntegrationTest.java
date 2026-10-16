package com.prueba.polizas.controller;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Flujos de renovación y cancelación por API. Cada prueba usa datos distintos de data.sql
 * para no depender del orden de ejecución.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PolizaFlujosIntegrationTest {

    private static final String API_KEY = "123456";
    /** Póliza 2 de data.sql: INDIVIDUAL, ACTIVA, canon 2.000.000 y 6 meses. */
    private static final long POLIZA_A_RENOVAR = 2L;
    /** Póliza 3 de data.sql: COLECTIVA, ACTIVA, con 3 riesgos activos. */
    private static final long POLIZA_A_CANCELAR = 3L;
    /** Póliza 4 de data.sql: COLECTIVA y CANCELADA. */
    private static final long POLIZA_CANCELADA = 4L;
    /** Riesgo 1 de data.sql: ACTIVO, de la póliza 1. */
    private static final long RIESGO_A_CANCELAR = 1L;
    private static final long POLIZA_INEXISTENTE = 99L;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void renovarPolizaActivaAjustaCanonYPrimaPorIpc() throws Exception {
        mockMvc.perform(post("/polizas/{id}/renovar", POLIZA_A_RENOVAR).header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RENOVADA"))
                .andExpect(jsonPath("$.canonMensual").value(2104000.00))
                .andExpect(jsonPath("$.prima").value(12624000.00));
    }

    @Test
    void cancelarPolizaCancelaTodosSusRiesgos() throws Exception {
        mockMvc.perform(post("/polizas/{id}/cancelar", POLIZA_A_CANCELAR).header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        mockMvc.perform(get("/polizas/{id}/riesgos", POLIZA_A_CANCELAR).header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].estado", everyItem(is("CANCELADO"))));
    }

    @Test
    void cancelarRiesgoDosVecesResponde409LaSegunda() throws Exception {
        mockMvc.perform(post("/riesgos/{id}/cancelar", RIESGO_A_CANCELAR).header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));

        mockMvc.perform(post("/riesgos/{id}/cancelar", RIESGO_A_CANCELAR).header("x-api-key", API_KEY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void renovarPolizaCanceladaResponde409() throws Exception {
        mockMvc.perform(post("/polizas/{id}/renovar", POLIZA_CANCELADA).header("x-api-key", API_KEY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("No se puede renovar una póliza cancelada"));
    }

    @Test
    void renovarPolizaInexistenteResponde404() throws Exception {
        mockMvc.perform(post("/polizas/{id}/renovar", POLIZA_INEXISTENTE).header("x-api-key", API_KEY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
