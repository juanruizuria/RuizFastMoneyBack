package com.ruiz.prestamos.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import com.ruiz.prestamos.persistence.enums.TipoImagen;
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
@Table(name = "imagenes")
@Getter
@Setter
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoImagen tipo;

    @Column(name = "url_imagen", nullable = false, length = 255)
    private String urlImagen;

    @ManyToOne
    @JoinColumn(name = "id_prestamo", referencedColumnName = "id", insertable = false, updatable = false)
    private Prestamo prestamo;

    @ManyToOne
    @JoinColumn(name = "id_garantia", referencedColumnName = "id", insertable = false, updatable = false)
    private Garantia garantia;

    @ManyToOne
    @JoinColumn(name = "id_pago", referencedColumnName = "id", insertable = false, updatable = false)
    private Pago pago;
}