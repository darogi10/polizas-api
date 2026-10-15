package com.prueba.polizas.domain;

import com.prueba.polizas.exception.ReglaNegocioException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado = EstadoPoliza.ACTIVA;

    @Column(nullable = false)
    private String tomador;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal canonMensual;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prima;

    @Column(nullable = false)
    private int mesesVigencia;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Riesgo> riesgos = new ArrayList<>();

    protected Poliza() { }

    public Poliza(TipoPoliza tipo, String tomador, BigDecimal canonMensual,
                  int mesesVigencia, LocalDate fechaInicio) {
        this.tipo = tipo;
        this.tomador = tomador;
        this.canonMensual = canonMensual.setScale(2, RoundingMode.HALF_UP);
        this.mesesVigencia = mesesVigencia;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaInicio.plusMonths(mesesVigencia);
        this.prima = calcularPrima();
    }

    private BigDecimal calcularPrima() {
        return canonMensual.multiply(BigDecimal.valueOf(mesesVigencia))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Renueva por el mismo periodo, ajustando el canon por IPC (en porcentaje, ej. 5.2). */
    public void renovar(BigDecimal ipcPorcentaje) {
        if (estado == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("No se puede renovar una póliza cancelada");
        }
        BigDecimal factor = BigDecimal.ONE.add(
                ipcPorcentaje.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        this.canonMensual = canonMensual.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        this.prima = calcularPrima();
        this.fechaInicio = this.fechaFin;
        this.fechaFin = this.fechaInicio.plusMonths(mesesVigencia);
        this.estado = EstadoPoliza.RENOVADA;
    }

    public void cancelar() {
        if (estado == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("La póliza " + id + " ya está cancelada");
        }
        this.estado = EstadoPoliza.CANCELADA;
        riesgos.forEach(Riesgo::cancelarSiActivo);
    }

    public Riesgo agregarRiesgo(String direccionInmueble, String arrendatario) {
        if (tipo != TipoPoliza.COLECTIVA) {
            throw new ReglaNegocioException(
                    "Solo las pólizas colectivas admiten agregar riesgos");
        }
        if (estado == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException(
                    "No se pueden agregar riesgos a una póliza cancelada");
        }
        Riesgo riesgo = new Riesgo(this, direccionInmueble, arrendatario);
        riesgos.add(riesgo);
        return riesgo;
    }

    public Long getId() { return id; }
    public TipoPoliza getTipo() { return tipo; }
    public EstadoPoliza getEstado() { return estado; }
    public String getTomador() { return tomador; }
    public BigDecimal getCanonMensual() { return canonMensual; }
    public BigDecimal getPrima() { return prima; }
    public int getMesesVigencia() { return mesesVigencia; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public List<Riesgo> getRiesgos() { return List.copyOf(riesgos); }
}
