package com.ruiz.prestamos.persistence.repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import com.ruiz.prestamos.persistence.entity.Documento;



public interface DocumentoRepository extends ListCrudRepository<Documento, Integer>{
    List<Documento> findByGarantiaId(Integer garantiaId);
}

