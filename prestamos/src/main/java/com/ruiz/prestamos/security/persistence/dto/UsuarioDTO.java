package com.ruiz.prestamos.security.persistence.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Integer id;
    private String usuario;
    private String nombre;
    private String contrasenia;
    private String correo;
    private Boolean bloqueada;
    private Boolean deshabilitada;
    private RolDTO rol;
}

