package com.prueba.polizas.controller;

import com.prueba.polizas.dto.EventoCoreRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Simula el endpoint del CORE para pruebas locales. */
@RestController
public class CoreMockController {

    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    @PostMapping("/core-mock/evento")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void recibirEvento(@RequestBody EventoCoreRequest request) {
        log.info("Evento {} para póliza {} enviado al CORE (mock)", request.evento(), request.polizaId());
    }
}
