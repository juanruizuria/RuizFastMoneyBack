package com.ruiz.prestamos.persistence.repository;

import org.springframework.data.repository.ListCrudRepository;
import com.ruiz.prestamos.persistence.entity.Persona;

public interface PersonaRepository extends ListCrudRepository<Persona,Integer>{

}


