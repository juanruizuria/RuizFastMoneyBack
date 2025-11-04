package com.ruiz.prestamos.persistence.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GarantiaDTO implements Serializable {
    private Integer id;
    private Integer idPrestamo;
    private Integer idPago;
    private String descripcion;
    private String estado;
    private String tipo;
    private BigDecimal valorEstimado;
    private LocalDate fechaRegistro;
    private List<DocumentoDTO> documentos;
    
}
