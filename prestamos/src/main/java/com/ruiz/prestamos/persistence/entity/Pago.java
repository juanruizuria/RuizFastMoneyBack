package com.ruiz.prestamos.persistence.entity;

import com.ruiz.prestamos.persistence.enums.EstadoPago;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pagos")
@Getter
@Setter
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "id_prestamo", referencedColumnName = "id", insertable = false, updatable = false)
    private Prestamo prestamo;
}
