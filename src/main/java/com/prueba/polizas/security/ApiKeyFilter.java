package com.prueba.polizas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Exige el header x-api-key en todas las rutas salvo /actuator/**. */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    static final String HEADER = "x-api-key";

    private final byte[] apiKeyEsperada;

    public ApiKeyFilter(@Value("${security.api-key:123456}") String apiKey) {
        this.apiKeyEsperada = apiKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        return ruta.equals("/actuator") || ruta.startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String apiKey = request.getHeader(HEADER);
        if (apiKey == null || apiKey.isBlank()) {
            rechazar(response, "Falta el header x-api-key");
            return;
        }
        // Comparación en tiempo constante para no filtrar información por timing.
        if (!MessageDigest.isEqual(apiKeyEsperada, apiKey.getBytes(StandardCharsets.UTF_8))) {
            rechazar(response, "El header x-api-key es inválido");
            return;
        }
        chain.doFilter(request, response);
    }

    private static void rechazar(HttpServletResponse response, String mensaje) throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"status\":" + status.value()
                + ",\"error\":\"" + status.getReasonPhrase()
                + "\",\"mensaje\":\"" + mensaje + "\"}");
    }
}
