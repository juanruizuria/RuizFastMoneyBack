package com.ruiz.prestamos.persistence.entity;



import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_prestamo", nullable = false)
    private Integer idPrestamo;

    @Column(name = "destinatario_email", nullable = false, length = 100)
    private String destinatarioEmail;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column(length = 255)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "id_prestamo", referencedColumnName = "id", insertable = false, updatable = false)
    private Prestamo prestamo;
}

