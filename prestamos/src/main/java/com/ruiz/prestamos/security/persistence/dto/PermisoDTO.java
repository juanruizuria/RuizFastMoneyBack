package com.ruiz.prestamos.security.persistence.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermisoDTO {
    private Integer id;
    private String nombre;
    private String label;
    private String icon;
    private String route;
    private Integer parentId;
}