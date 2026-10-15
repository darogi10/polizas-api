package com.prueba.polizas.repository;

import com.prueba.polizas.domain.Riesgo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {

    List<Riesgo> findByPolizaId(Long polizaId);
}
