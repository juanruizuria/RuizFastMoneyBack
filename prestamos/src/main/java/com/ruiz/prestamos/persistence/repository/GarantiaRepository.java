package com.ruiz.prestamos.persistence.repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import com.ruiz.prestamos.persistence.entity.Garantia;



public interface GarantiaRepository extends ListCrudRepository<Garantia,Integer>{
    List<Garantia> findByPrestamoId(Integer prestamoId);
    List<Garantia> findByPagoId(Integer pagoId);
}

