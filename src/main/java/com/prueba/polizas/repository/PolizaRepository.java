package com.prueba.polizas.repository;

import com.prueba.polizas.domain.EstadoPoliza;
import com.prueba.polizas.domain.Poliza;
import com.prueba.polizas.domain.TipoPoliza;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    /** Filtra por tipo y estado; un parámetro nulo no aplica filtro. */
    @Query("""
            select p from Poliza p
            where (:tipo is null or p.tipo = :tipo)
              and (:estado is null or p.estado = :estado)
            order by p.id
            """)
    List<Poliza> buscar(@Param("tipo") TipoPoliza tipo, @Param("estado") EstadoPoliza estado);
}
