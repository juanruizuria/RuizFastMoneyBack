package com.ruiz.prestamos.persistence.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonaDTO implements Serializable{
    private Integer id;
    private String nombre;
    private String documento;
    private String email;
    private String telefono;
    private String direccion;
    private String tipo;
    private Boolean activo;
    LocalDateTime fechaCreacion;
    LocalDateTime fechaModificacion;
}
