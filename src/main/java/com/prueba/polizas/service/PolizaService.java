package com.prueba.polizas.service;

import com.prueba.polizas.domain.EstadoPoliza;
import com.prueba.polizas.domain.Poliza;
import com.prueba.polizas.domain.Riesgo;
import com.prueba.polizas.domain.TipoPoliza;
import com.prueba.polizas.dto.AgregarRiesgoRequest;
import com.prueba.polizas.dto.PolizaResponse;
import com.prueba.polizas.dto.RiesgoResponse;
import com.prueba.polizas.exception.RecursoNoEncontradoException;
import com.prueba.polizas.repository.PolizaRepository;
import com.prueba.polizas.repository.RiesgoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final ApplicationEventPublisher eventos;
    private final BigDecimal ipcPorcentaje;

    public PolizaService(PolizaRepository polizaRepository,
                         RiesgoRepository riesgoRepository,
                         ApplicationEventPublisher eventos,
                         @Value("${polizas.ipc:5.2}") BigDecimal ipcPorcentaje) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
        this.eventos = eventos;
        this.ipcPorcentaje = ipcPorcentaje;
    }

    public List<PolizaResponse> listar(TipoPoliza tipo, EstadoPoliza estado) {
        return polizaRepository.buscar(tipo, estado).stream()
                .map(PolizaResponse::desde)
                .toList();
    }

    public PolizaResponse obtener(Long id) {
        return PolizaResponse.desde(buscarPoliza(id));
    }

    public List<RiesgoResponse> listarRiesgos(Long polizaId) {
        if (!polizaRepository.existsById(polizaId)) {
            throw polizaNoEncontrada(polizaId);
        }
        return riesgoRepository.findByPolizaId(polizaId).stream()
                .map(RiesgoResponse::desde)
                .toList();
    }

    @Transactional
    public PolizaResponse renovar(Long id) {
        Poliza poliza = buscarPoliza(id);
        poliza.renovar(ipcPorcentaje);
        eventos.publishEvent(new EventoNegocio(TipoEventoCore.RENOVACION, poliza.getId()));
        return PolizaResponse.desde(poliza);
    }

    @Transactional
    public PolizaResponse cancelar(Long id) {
        Poliza poliza = buscarPoliza(id);
        poliza.cancelar();
        eventos.publishEvent(new EventoNegocio(TipoEventoCore.CANCELACION, poliza.getId()));
        return PolizaResponse.desde(poliza);
    }

    @Transactional
    public RiesgoResponse agregarRiesgo(Long polizaId, AgregarRiesgoRequest request) {
        Poliza poliza = buscarPoliza(polizaId);
        Riesgo riesgo = poliza.agregarRiesgo(request.direccionInmueble(), request.arrendatario());
        riesgoRepository.save(riesgo); // persiste ya para disponer del id generado
        eventos.publishEvent(new EventoNegocio(TipoEventoCore.RIESGO_AGREGADO, poliza.getId()));
        return RiesgoResponse.desde(riesgo);
    }

    @Transactional
    public RiesgoResponse cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el riesgo con id " + riesgoId));
        riesgo.cancelar();
        Long polizaId = riesgo.getPoliza().getId();
        eventos.publishEvent(new EventoNegocio(TipoEventoCore.RIESGO_CANCELADO, polizaId));
        return RiesgoResponse.desde(riesgo);
    }

    private Poliza buscarPoliza(Long id) {
        return polizaRepository.findById(id).orElseThrow(() -> polizaNoEncontrada(id));
    }

    private static RecursoNoEncontradoException polizaNoEncontrada(Long id) {
        return new RecursoNoEncontradoException("No existe la póliza con id " + id);
    }
}
