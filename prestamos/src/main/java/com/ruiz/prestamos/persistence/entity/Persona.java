package com.ruiz.prestamos.persistence.entity;

import java.util.Date;

import com.ruiz.prestamos.persistence.enums.TipoUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "personas")
@Getter
@Setter
@NoArgsConstructor
public class Persona {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario tipo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "documento_identidad", unique = true, nullable = false, length = 20)
    private String documentoIdentidad;

    @Column(length = 100)
    private String email;

    @Column(length = 15)
    private String telefono;

    @Column(length = 150)
    private String direccion;
}

