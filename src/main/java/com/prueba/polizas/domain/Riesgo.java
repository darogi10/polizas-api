package com.prueba.polizas.domain;

import com.prueba.polizas.exception.ReglaNegocioException;
import jakarta.persistence.*;

@Entity
@Table(name = "riesgos")
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id")
    private Poliza poliza;

    @Column(nullable = false)
    private String direccionInmueble;

    @Column(nullable = false)
    private String arrendatario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRiesgo estado = EstadoRiesgo.ACTIVO;

    protected Riesgo() { }

    Riesgo(Poliza poliza, String direccionInmueble, String arrendatario) {
        this.poliza = poliza;
        this.direccionInmueble = direccionInmueble;
        this.arrendatario = arrendatario;
    }

    public void cancelar() {
        if (estado == EstadoRiesgo.CANCELADO) {
            throw new ReglaNegocioException("El riesgo " + id + " ya está cancelado");
        }
        this.estado = EstadoRiesgo.CANCELADO;
    }

    /** Uso interno de la póliza: cancela sin fallar si ya estaba cancelado. */
    void cancelarSiActivo() {
        this.estado = EstadoRiesgo.CANCELADO;
    }

    public Long getId() { return id; }
    public Poliza getPoliza() { return poliza; }
    public String getDireccionInmueble() { return direccionInmueble; }
    public String getArrendatario() { return arrendatario; }
    public EstadoRiesgo getEstado() { return estado; }
}
