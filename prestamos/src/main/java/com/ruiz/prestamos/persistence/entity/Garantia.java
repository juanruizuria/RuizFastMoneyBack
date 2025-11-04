package com.ruiz.prestamos.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.ruiz.prestamos.persistence.enums.EstadoGarantia;
import com.ruiz.prestamos.persistence.enums.TipoGarantia;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoGarantia estado = EstadoGarantia.EN_CUSTODIA;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDate fechaRegistro = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "id_prestamo", referencedColumnName = "id")
    private Prestamo prestamo;

    @ManyToOne
    @JoinColumn(name = "id_pago", referencedColumnName = "id")
    private Pago pago;

    @OneToMany(mappedBy = "garantia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Documento> documentos;
}
