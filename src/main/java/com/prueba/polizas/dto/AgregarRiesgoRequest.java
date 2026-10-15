package com.prueba.polizas.dto;

import jakarta.validation.constraints.NotBlank;

public record AgregarRiesgoRequest(
        @NotBlank(message = "es obligatoria") String direccionInmueble,
        @NotBlank(message = "es obligatorio") String arrendatario) {
}
