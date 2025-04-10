package com.ruiz.prestamos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.ruiz.prestamos.config.GenericMapper;
import com.ruiz.prestamos.controller.ApiResponse;
import com.ruiz.prestamos.persistence.dto.PagoDTO;
import com.ruiz.prestamos.persistence.entity.Pago;
import com.ruiz.prestamos.persistence.entity.Prestamo;
import com.ruiz.prestamos.persistence.enums.EstadoPago;
import com.ruiz.prestamos.persistence.repository.PagoRepository;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PrestamoService prestamoService;
    private GenericMapper mapper;

    public PagoService(PagoRepository pagoRepository, @Lazy PrestamoService prestamoService, GenericMapper mapper) {
        this.pagoRepository = pagoRepository;
        this.prestamoService = prestamoService;
        this.mapper = mapper;
    }

    public ApiResponse<List<PagoDTO>> getAll() {
        try {
            List<Pago> pagos = pagoRepository.findAll();
            if (pagos.isEmpty()) {
                throw new NoSuchElementException("No existen pagos");
            }
            return ApiResponse.success("pagos encontrados", mapper.convertirListaADTO(pagos, PagoDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<List<PagoDTO>> getByPrestamo(int idPrestamo) {
        try {
            List<Pago> pagos = pagoRepository.findByPrestamoId(idPrestamo);
            if (pagos.isEmpty()) {
                throw new NoSuchElementException("No existen pagos para el prestamo con id: " + idPrestamo);
            }
            return ApiResponse.success("pagos encontrados", mapper.convertirListaADTO(pagos, PagoDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }

    }

    public ApiResponse<PagoDTO> get(int id) {
        try {
            Pago pago = pagoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe pago con id: " + id));
            return ApiResponse.success("pago encontrado", mapper.convertirADTO(pago, PagoDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<PagoDTO> save(PagoDTO inputPago) {
        try {
            if (inputPago.getIdPrestamo() == null || inputPago.getIdPrestamo() <= 0) {
                throw new NoSuchElementException("No existe prestamo con id: " + inputPago.getIdPrestamo());
            }
            if (inputPago.getMonto() == null || inputPago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new NoSuchElementException("El monto es requerido");
            }
            Pago pago = mapper.convertirAEntidad(inputPago, Pago.class);
            if (!exist(inputPago.getId())) {// insert
                pago.setFechaVencimiento(LocalDate.now());
                pago.setEstado(EstadoPago.PAGADA);
            }
            BigDecimal montoCalulado = prestamoService.getMontoInteres(inputPago.getIdPrestamo());
            if (inputPago.getMonto().compareTo(montoCalulado) != 0) {
                throw new NoSuchElementException("El monto invalido, el monto correcto es: " + montoCalulado);
            }
            pago.setFechaPago(LocalDate.now());
            pagoRepository.save(pago);
            return ApiResponse.success("pago guardada", mapper.convertirADTO(pago, PagoDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<PagoDTO> update(PagoDTO inputPago) {
        try {
            if (!exist(inputPago.getId())) {
                throw new NoSuchElementException("No existe pago con id: " + inputPago.getId());
            }
            if (inputPago.getMonto() == null || inputPago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new NoSuchElementException("El monto es requerido");
            }
            Pago pago = mapper.convertirAEntidad(inputPago, Pago.class);
            pagoRepository.save(pago);
            return ApiResponse.success("pago modificado", mapper.convertirADTO(pago, PagoDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<PagoDTO> delete(int id) {
        try {
            Pago pago = pagoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe pago con id: " + id));
            pagoRepository.deleteById(id);
            return ApiResponse.success("pago eliminado", mapper.convertirADTO(pago, PagoDTO.class));

        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public boolean exist(Integer id) {
        boolean result = false;
        if (id != null && pagoRepository.existsById(id)) {
            result = true;
        }
        return result;
    }

    public void generarPagos(Prestamo prestamo, LocalDate fechaInicio, int meses) {
        try {
            List<Pago> pagos = new ArrayList<>();
            for (int i = 1; i <= meses; i++) {
                Pago pago = new Pago();
                pago.setPrestamo(prestamo);
                pago.setFechaVencimiento(fechaInicio.plusMonths(i));
                pago.setFechaPago(null);
                pago.setEstado(EstadoPago.PENDIENTE);
                pago.setMonto(BigDecimal.ZERO);
                pagos.add(pago);
            }
            pagoRepository.saveAll(pagos);
        } catch (Exception e) {
            System.out.println("Error al generar pagos: " + e.getMessage());
        }
    }

}
