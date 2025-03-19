package com.ruiz.prestamos.persistence.repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import com.ruiz.prestamos.persistence.entity.Pago;

public interface PagoRepository extends ListCrudRepository<Pago, Integer> {

    List<Pago> findByPrestamoId(Integer idPrestamo);

}
