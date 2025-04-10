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
public class PrestamoDTO implements Serializable {
    private Integer id;
    private PersonaDTO prestatario;
    private PersonaDTO prestador;
    private BigDecimal monto;
    private BigDecimal interes;
    private Integer meses;
    private LocalDate fechaInicio;
    private LocalDate fechaLimite;
    private String estado;
    private BigDecimal penalizacion;
    private List<PagoDTO> pagos;
}
