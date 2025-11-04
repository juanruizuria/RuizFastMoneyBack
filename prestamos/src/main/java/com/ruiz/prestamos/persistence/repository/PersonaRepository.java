package com.ruiz.prestamos.persistence.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import com.ruiz.prestamos.persistence.entity.Persona;
import com.ruiz.prestamos.persistence.interfaces.PersonaRepositoryCustom;

public interface PersonaRepository extends ListCrudRepository<Persona, Integer>, PersonaRepositoryCustom {

      List<Persona> findByActivoTrueOrderByNombreAsc();

    List<Persona> findByActivoAndFechaCreacionBetween(
            Boolean activo,
            Date fechaInicio,
            Date fechaFin);

    @Query(value = """
                SELECT * FROM personas p
                WHERE (p.tipo = :tipo)
                  AND (p.activo = :activo)
                  AND (p.fecha_creacion BETWEEN :fechaInicio AND :fechaFin)
            """, nativeQuery = true)
    List<Persona> listarFiltro(
            @Param("tipo") String tipo,
            @Param("activo") Boolean activo,
            @Param("fechaInicio") Date fechaInicio,
            @Param("fechaFin") Date fechaFin);

}
