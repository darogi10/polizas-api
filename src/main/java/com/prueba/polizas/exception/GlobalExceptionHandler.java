package com.prueba.polizas.exception;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> reglaNegocio(ReglaNegocioException e) {
        return respuesta(HttpStatus.CONFLICT, e.getMessage(), null);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException e) {
        return respuesta(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fe -> campos.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        return respuesta(HttpStatus.BAD_REQUEST, "La solicitud tiene campos inválidos", campos);
    }

    /** Parámetros de ruta o query con tipo inválido (ej. tipo=OTRO o id=abc). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> tipoInvalido(MethodArgumentTypeMismatchException e) {
        String mensaje = "Valor inválido '" + e.getValue() + "' para el parámetro '" + e.getName() + "'";
        Class<?> tipo = e.getRequiredType();
        if (tipo != null && tipo.isEnum()) {
            mensaje += ". Valores permitidos: " + Arrays.toString(tipo.getEnumConstants());
        }
        return respuesta(HttpStatus.BAD_REQUEST, mensaje, null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> cuerpoIlegible(HttpMessageNotReadableException e) {
        return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es un JSON válido", null);
    }

    private static ResponseEntity<ErrorResponse> respuesta(HttpStatus status, String mensaje,
                                                           Map<String, String> campos) {
        ErrorResponse cuerpo = new ErrorResponse(
                Instant.now(), status.value(), status.getReasonPhrase(), mensaje, campos);
        return ResponseEntity.status(status).body(cuerpo);
    }
}
