package com.prueba.polizas.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PolizaControllerIntegrationTest {

    private static final String API_KEY = "123456";
    /** Póliza 1 de data.sql: INDIVIDUAL y ACTIVA. */
    private static final long POLIZA_INDIVIDUAL = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sinApiKeyResponde401() throws Exception {
        mockMvc.perform(get("/polizas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void listarPolizasConApiKeyResponde200() throws Exception {
        mockMvc.perform(get("/polizas").header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void agregarRiesgoAPolizaIndividualResponde409() throws Exception {
        mockMvc.perform(post("/polizas/{id}/riesgos", POLIZA_INDIVIDUAL)
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"direccionInmueble": "Calle 10 # 20-30", "arrendatario": "Ana Díaz"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.mensaje").value("Solo las pólizas colectivas admiten agregar riesgos"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
