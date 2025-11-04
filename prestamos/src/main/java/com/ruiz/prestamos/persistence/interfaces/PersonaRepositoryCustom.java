package com.ruiz.prestamos.persistence.interfaces;

import java.sql.Date;
import java.util.List;

import com.ruiz.prestamos.persistence.entity.Persona;

public interface PersonaRepositoryCustom {
    List<Persona> listarFiltroDinamico(String tipo, Boolean activo, Date fechaInicio, Date fechaFin);
}
