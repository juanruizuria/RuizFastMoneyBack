package com.ruiz.prestamos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruiz.prestamos.config.GenericMapper;
import com.ruiz.prestamos.controller.ApiResponse;

import com.ruiz.prestamos.persistence.dto.PrestamoDTO;
import com.ruiz.prestamos.persistence.entity.Pago;
import com.ruiz.prestamos.persistence.entity.Prestamo;
import com.ruiz.prestamos.persistence.enums.EstadoPrestamo;
import com.ruiz.prestamos.persistence.repository.PagoRepository;
import com.ruiz.prestamos.persistence.repository.PrestamoRepository;

@Service
public class PrestamoService {

    private final PersonaService personaService;
    private final PrestamoRepository prestamoRepository;
    private PagoService pagoService;
    private PagoRepository pagoRepository;
    private GenericMapper mapper;

    public PrestamoService(PrestamoRepository prestamoRepository,
            PersonaService personaService,
            PagoService pagoService,
            GenericMapper mapper) {
        this.prestamoRepository = prestamoRepository;
        this.pagoService = pagoService;
        this.mapper = mapper;
        this.personaService = personaService;

    }

    public ApiResponse<List<PrestamoDTO>> getAll() {
        try {
            List<Prestamo> prestamos = prestamoRepository.findAll();
            if (prestamos.isEmpty()) {
                throw new NoSuchElementException("No existen prestamos");
            }
            return ApiResponse.success("prestamos encontrados",
                    mapper.convertirListaADTO(prestamos, PrestamoDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<PrestamoDTO> get(int id) {
        try {
            Prestamo prestamo = prestamoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe prestamo con id: " + id));
            return ApiResponse.success("prestamo encontrada", mapper.convertirADTO(prestamo, PrestamoDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    @Transactional()
    public ApiResponse<PrestamoDTO> save(PrestamoDTO prestamoDTO) {
        LocalDate fechaInicio = prestamoDTO.getFechaInicio();
        int meses = prestamoDTO.getMeses();
        try {
            Prestamo prestamo = mapper.convertirAEntidad(prestamoDTO, Prestamo.class);
            if (exist(prestamoDTO.getId())) {// update
                Prestamo prestamoDB = prestamoRepository.findById(prestamoDTO.getId()).orElse(null);
                List<Pago> pagosRealizados = pagoRepository.findByPrestamoIdAndMontoGreaterThan(prestamoDTO.getId(),
                        BigDecimal.ZERO);
                if (!prestamoDTO.getFechaInicio().isEqual(prestamoDB.getFechaInicio())) {
                    throw new Exception("No se puede cambiar la fecha de inicio");
                }
                if (prestamoDTO.getMeses() < pagosRealizados.size()) {
                    throw new Exception("No se puede reducir el número de meses, tiene pagos realizados");
                }
                if (prestamoDTO.getMeses() < prestamoDB.getMeses()) {
                    throw new Exception("No se puede reducir el número de meses");
                }
                // se generan automaticamente los pagos
                fechaInicio = pagoRepository.findFirstByPrestamoIdOrderByFechaVencimientoDesc(prestamoDTO.getId()).get()
                        .getFechaVencimiento();
                if (fechaInicio == null) {
                    throw new Exception("No se generaron los pagos automaticamente. Revise");
                }
                // si aumenta los meses se generaran mas pagos
                meses = prestamoDTO.getMeses() - prestamoDB.getMeses();
            }
            prestamo.setEstado(EstadoPrestamo.PENDIENTE);
            prestamo.setFechaLimite(fechaInicio.plusMonths(prestamoDTO.getMeses()));
            prestamo.setPrestador(personaService.getById(prestamo.getPrestador().getId()));
            prestamo.setPrestatario(personaService.getById(prestamo.getPrestatario().getId()));
            Prestamo newPrestamo = prestamoRepository.save(prestamo);
            pagoService.generarPagos(newPrestamo, fechaInicio, meses);
            return ApiResponse.success("Prestamo guardado", mapper.convertirADTO(newPrestamo, PrestamoDTO.class));
        } catch (Exception e) {
            System.out.println("Error al crear el prestamo: " + e.getMessage());
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<Boolean> delete(int id) {
        try {
            prestamoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe prestamo con id: " + id));
            prestamoRepository.deleteById(id);
            return ApiResponse.success("prestamo eliminado", true);
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), false);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public boolean exist(Integer id) {
        boolean result = false;
        if (id != null && prestamoRepository.existsById(id)) {
            result = true;
        }
        return result;
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
}