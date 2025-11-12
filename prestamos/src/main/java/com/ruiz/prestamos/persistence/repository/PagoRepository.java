package com.ruiz.prestamos.persistence.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;
import com.ruiz.prestamos.persistence.entity.Pago;
import com.ruiz.prestamos.persistence.enums.EstadoPago;

public interface PagoRepository extends ListCrudRepository<Pago, Integer> {

    List<Pago> findByPrestamoId(Integer idPrestamo);

    // Listar pagos por préstamo donde el monto es mayor a ...
    List<Pago> findByPrestamoIdAndMontoGreaterThan(Integer prestamoId, BigDecimal monto);

    // obtener el último pago realizado por un préstamo
    Optional<Pago> findFirstByPrestamoIdOrderByFechaPagoDesc(Integer prestamoId);

    // obtener el último pago realizado por fecha de vencimiento (generada
    // automaticamente)
    Optional<Pago> findFirstByPrestamoIdOrderByFechaVencimientoDesc(Integer prestamoId);

    // Listar pagos por préstamo con fecha de vencimiento antes de una fecha dada
    List<Pago> findByPrestamoIdAndFechaVencimientoBefore(Integer prestamoId, LocalDate fechaVencimiento);

    // Devuelve el último pago con estado PAGADA, según fecha de vencimiento
    Optional<Pago> findTopByPrestamoIdAndEstadoOrderByFechaVencimientoDesc(Integer prestamoId, EstadoPago estado);

    List<Pago> findByEstadoAndFechaVencimientoBefore(EstadoPago estado, LocalDate fecha);

}
