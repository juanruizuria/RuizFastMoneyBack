package com.ruiz.prestamos.persistence.repository;

import com.ruiz.prestamos.persistence.entity.Notificacion;
import org.springframework.data.repository.ListCrudRepository;

public interface NotificacionRepository extends ListCrudRepository<Notificacion, Integer> {

    Boolean existsByIdPrestamoAndDescripcionAndFechaEnvioBetween(
        Integer idPrestamo, 
        String descripcion,
        java.time.LocalDateTime inicio, 
        java.time.LocalDateTime fin
    );
    
}
