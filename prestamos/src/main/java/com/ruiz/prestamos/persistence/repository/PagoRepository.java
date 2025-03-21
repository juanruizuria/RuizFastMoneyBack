package com.ruiz.prestamos.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;
import com.ruiz.prestamos.persistence.entity.Pago;

public interface PagoRepository extends ListCrudRepository<Pago, Integer> {

    List<Pago> findByPrestamoId(Integer idPrestamo);
    // Listar pagos por préstamo donde el monto es mayor a ...
    List<Pago> findByPrestamoIdAndMontoGreaterThan(Integer prestamoId, BigDecimal monto);
    // obtener el último pago realizado por un préstamo
    Optional<Pago> findFirstByPrestamoIdOrderByFechaPagoDesc(Integer prestamoId);

}
