package com.ruiz.prestamos.persistence.repository;

import org.springframework.data.repository.ListCrudRepository;
import com.ruiz.prestamos.persistence.entity.Prestamo;


public interface PrestamoRepository extends ListCrudRepository<Prestamo,Integer>{

    
}

