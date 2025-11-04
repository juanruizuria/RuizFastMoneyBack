package com.ruiz.prestamos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsUtils;

import com.ruiz.prestamos.config.GenericMapper;
import com.ruiz.prestamos.persistence.dto.PagoDTO;
import com.ruiz.prestamos.persistence.entity.Pago;
import com.ruiz.prestamos.persistence.entity.Prestamo;
import com.ruiz.prestamos.persistence.enums.EstadoPago;
import com.ruiz.prestamos.persistence.enums.EstadoPrestamo;
import com.ruiz.prestamos.persistence.repository.PagoRepository;
import com.ruiz.prestamos.persistence.repository.PrestamoRepository;
import com.ruiz.prestamos.util.ApiResponse;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PrestamoRepository prestamoRepository;
    private GenericMapper mapper;

    public PagoService(PagoRepository pagoRepository, @Lazy PrestamoRepository prestamoRepository,
            GenericMapper mapper) {
        this.pagoRepository = pagoRepository;
        this.prestamoRepository = prestamoRepository;
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

    public ApiResponse<PagoDTO> pagar(PagoDTO input) {
        try {
            if (!exist(input.getId())) {
                return ApiResponse.error("No existe pago");
            }
            Pago pago = pagoRepository.findById(input.getId())
                    .orElseThrow(() -> new NoSuchElementException("No existe pago con id: " + input.getId()));

            if (!puedePagar(pago)) {
                return ApiResponse.error("No puede pagar, tiene pagos anteriores pendientes");
            }
            pago.setFechaPago(LocalDate.now());
            BigDecimal montoCalulado = this.getMontoInteres(input.getIdPrestamo());
            pago.setMonto(montoCalulado);
            pago.setEstado(EstadoPago.PAGADA);
            pagoRepository.save(pago);

            //Verificamos si todas estan pagadas para cambiar de estado al prestamo
            Prestamo prestamo = pago.getPrestamo();
            List<Pago> todosPagos = pagoRepository.findByPrestamoId(prestamo.getId());
            boolean todasPagadas = todosPagos.stream()
                    .allMatch(p -> p.getEstado() == EstadoPago.PAGADA);

            if (todasPagadas) {
                prestamo.setEstado(EstadoPrestamo.PAGADO);
                prestamoRepository.save(prestamo);
            }

            return ApiResponse.success("pago realizado", mapper.convertirADTO(pago, PagoDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("Error interno del servidor: ");
        }
    }

    public BigDecimal getMontoInteres(int idPrestasmo) {
        try {
            Prestamo prestamo = prestamoRepository.findById(idPrestasmo)
                    .orElseThrow(() -> new NoSuchElementException("No existe prestamo con id: " + idPrestasmo));
            return prestamo.getMonto().multiply(prestamo.getInteres()).divide(new BigDecimal(100));
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("Error al obtener el monto de interes: " + e.getMessage());
            return null;
        }

    }

    public boolean puedePagar(Pago pago) {
        // Traemos todos los pagos anteriores
        List<Pago> pagosAnteriores = pagoRepository.findByPrestamoIdAndFechaVencimientoBefore(
                pago.getPrestamo().getId(),
                pago.getFechaVencimiento());

        // Verificamos si alguno sigue pendiente
        boolean result = pagosAnteriores.stream().allMatch(p -> "PAGADA".equals(p.getEstado().name()));
        return result;
    }

    public ApiResponse<PagoDTO> delete(int id) {
        try {
            if (!exist(id)) {
                return ApiResponse.error("No existe pago");
            }
            Pago pago = pagoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe pago con id: " + id));

            // Buscar el último pago PAGADA de este préstamo
            Pago ultimoPagado = pagoRepository
                    .findTopByPrestamoIdAndEstadoOrderByFechaVencimientoDesc(pago.getPrestamo().getId(),
                            EstadoPago.PAGADA)
                    .orElseThrow(() -> new RuntimeException("No hay pagos registrados para eliminar"));

            if (!ultimoPagado.getId().equals(pago.getId())) {
                return ApiResponse.error("Solo puede eliminar el último pago registrado");
            }

            pago.setFechaPago(null);
            pago.setMonto(BigDecimal.ZERO);
            pago.setEstado(EstadoPago.PENDIENTE);
            pagoRepository.save(pago);

            // Verificar si todas las cuotas siguen pagadas
            Prestamo prestamo = pago.getPrestamo();
            boolean todasPagadas = prestamo.getPagos().stream()
                    .allMatch(p -> p.getEstado() == EstadoPago.PAGADA);

            if (!todasPagadas) {
                prestamo.setEstado(EstadoPrestamo.PENDIENTE); // 👈 lo devolvemos a pendiente
                prestamoRepository.save(prestamo);
            }

            return ApiResponse.success("pago eliminado", mapper.convertirADTO(pago, PagoDTO.class));

        } catch (NoSuchElementException e) {
            System.out.println("Error al eliminar el pago (NoSuchElementException): " + e.getMessage());
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            System.out.println("Error al eliminar el pago: " + e.getMessage());
            return ApiResponse.error("Error interno del servidor: ");
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
