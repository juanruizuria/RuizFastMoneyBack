package com.ruiz.prestamos.persistence.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PagoDTO implements Serializable{
    private Integer id;
    private LocalDate fechaVencimiento;
    private BigDecimal monto;
    private LocalDate fechaPago;
    private String estado;
    private Integer idPrestamo;
}
