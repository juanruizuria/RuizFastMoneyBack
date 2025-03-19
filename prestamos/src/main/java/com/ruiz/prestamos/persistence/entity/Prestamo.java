package com.ruiz.prestamos.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.ruiz.prestamos.persistence.enums.EstadoPrestamo;

@Entity
@Table(name = "prestamos")
@Getter
@Setter
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interes;

    @Column(nullable = false)
    private Integer meses;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaLimite;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoPrestamo estado = EstadoPrestamo.PENDIENTE;

    @Column(precision = 5, scale = 2)
    private BigDecimal penalizacion = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "id_prestatario", referencedColumnName = "id", insertable = false, updatable = false)
    private Usuario prestatario;

    @OneToOne
    @JoinColumn(name = "id_prestador", referencedColumnName = "id", insertable = false, updatable = false)
    private Usuario prestador;

    @OneToMany(mappedBy = "prestamo")
    private List<Pago> pagos;

    @OneToMany(mappedBy = "prestamo")
    private List<Garantia> garantias;

    
}



