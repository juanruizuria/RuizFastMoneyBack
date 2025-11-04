package com.ruiz.prestamos.persistence.entity;

import com.ruiz.prestamos.persistence.audit.Auditable;
import com.ruiz.prestamos.persistence.enums.EstadoPago;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Pago extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "id_prestamo", referencedColumnName = "id", nullable = false)
    private Prestamo prestamo;

    @OneToMany(mappedBy = "pago")
    private List<Garantia> garantias;
}
