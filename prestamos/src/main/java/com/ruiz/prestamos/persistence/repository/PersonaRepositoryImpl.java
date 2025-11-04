package com.ruiz.prestamos.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import com.ruiz.prestamos.persistence.entity.Persona;
import com.ruiz.prestamos.persistence.interfaces.PersonaRepositoryCustom;

import java.sql.Date;
import java.util.List;

@Repository
public class PersonaRepositoryImpl implements PersonaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Persona> listarFiltroDinamico(String tipo, Boolean activo, Date fechaInicio, Date fechaFin) {
        String sql = "SELECT * FROM personas p WHERE 1=1";

        if (tipo != null) {
            sql += " AND p.tipo = '" + tipo + "'";
        }
        if (activo != null) {
            sql += " AND p.activo = " + (activo ? "true" : "false");
        }
        if (fechaInicio != null) {
            sql += " AND date(p.fecha_creacion) >= '" + fechaInicio + "'";
        }
        if (fechaFin != null) {
            sql += " AND date(p.fecha_creacion) <= '" + fechaFin + "'";
        }
        sql += " ORDER BY p.nombre DESC";
        System.out.println("SQL Query: " + sql); // Debugging line to check the generated SQL

        Query query = entityManager.createNativeQuery(sql, Persona.class);
        return query.getResultList();
    }
}
