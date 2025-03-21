package com.ruiz.prestamos.persistence.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonaDTO {
    private Integer id;
    private String nombre;
    private String documento;
    private String email;
    private String telefono;
    private String direccion;
    private String tipo;
}
