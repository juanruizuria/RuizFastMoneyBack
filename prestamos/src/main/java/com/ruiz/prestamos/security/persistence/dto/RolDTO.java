package com.ruiz.prestamos.security.persistence.dto;

import java.util.Set;

import lombok.Data;

@Data
public class RolDTO {
    private Integer id;
    private String nombre;
    private Set<Integer> permisosIds;
}
