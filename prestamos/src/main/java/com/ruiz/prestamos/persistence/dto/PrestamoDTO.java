package com.ruiz.prestamos.persistence.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PrestamoDTO {
    private Integer id;
    private Integer idPrestatario;
    private Integer idPrestador;
    private BigDecimal monto;
    private BigDecimal interes;
    private Integer meses;
    private LocalDate fechaInicio;
    private LocalDate fechaLimite;
    private String estado;
    private BigDecimal penalizacion;
}
