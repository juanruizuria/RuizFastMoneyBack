package com.ruiz.prestamos.persistence.entity;

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

import com.ruiz.prestamos.persistence.audit.Auditable;
import com.ruiz.prestamos.persistence.enums.EstadoPrestamo;

@Entity
@Table(name = "prestamos")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Prestamo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interes;

    @Column(nullable = false)
    private Integer meses;

    @Column(name = "fecha_inicio",  nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoPrestamo estado = EstadoPrestamo.PENDIENTE;

    @Column(precision = 5, scale = 2)
    private BigDecimal penalizacion = BigDecimal.ZERO;
    
    @ManyToOne
    @JoinColumn(name = "id_prestatario", referencedColumnName = "id", nullable = false)
    private Persona prestatario;
    
    @ManyToOne
    @JoinColumn(name = "id_prestador", referencedColumnName = "id", nullable = false)
    private Persona prestador;

    @OneToMany(mappedBy = "prestamo")
    private List<Pago> pagos;

    @OneToMany(mappedBy = "prestamo")
    private List<Garantia> garantias;

    
}



