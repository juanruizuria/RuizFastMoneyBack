package com.ruiz.prestamos.persistence.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoDTO {

    private Integer id;
    private String nombre;
    private String ruta;
    private String tipo;
    private Long size;
    private Integer garantiaId;

}
