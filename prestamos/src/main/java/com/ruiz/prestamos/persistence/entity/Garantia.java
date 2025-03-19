package com.ruiz.prestamos.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ruiz.prestamos.persistence.enums.EstadoGarantia;
import com.ruiz.prestamos.persistence.enums.TipoGarantia;

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
@Table(name = "garantias")
@Getter
@Setter
public class Garantia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoGarantia tipo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorEstimado;

    @Column(length = 255)
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoGarantia estado = EstadoGarantia.EN_CUSTODIA;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_prestamo", referencedColumnName = "id", insertable = false, updatable = false)
    private Prestamo prestamo;
}
