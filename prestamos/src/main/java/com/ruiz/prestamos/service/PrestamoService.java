package com.ruiz.prestamos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruiz.prestamos.config.GenericMapper;
import com.ruiz.prestamos.persistence.dto.PrestamoDTO;
import com.ruiz.prestamos.persistence.entity.Pago;
import com.ruiz.prestamos.persistence.entity.Prestamo;
import com.ruiz.prestamos.persistence.enums.EstadoPrestamo;
import com.ruiz.prestamos.persistence.repository.PagoRepository;
import com.ruiz.prestamos.persistence.repository.PrestamoRepository;
import com.ruiz.prestamos.util.ApiResponse;

@Service
public class PrestamoService {

    private final PersonaService personaService;
    private final PrestamoRepository prestamoRepository;
    private PagoService pagoService;
    private PagoRepository pagoRepository;
    private GenericMapper mapper;

    public PrestamoService(PrestamoRepository prestamoRepository,
            PagoRepository pagoRepository,
            PersonaService personaService,
            PagoService pagoService,
            GenericMapper mapper) {
        this.prestamoRepository = prestamoRepository;
        this.pagoRepository = pagoRepository;
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
     
            List<PrestamoDTO> prestamosDTO = mapper.convertirListaADTO(prestamos, PrestamoDTO.class);

            return ApiResponse.success("Prestamos encontrados", prestamosDTO);
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
            return ApiResponse.success("prestamo encontrado", mapper.convertirADTO(prestamo, PrestamoDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    @Transactional()
    public ApiResponse<PrestamoDTO> save(PrestamoDTO prestamoDTO) {
        try {
            if (prestamoDTO.getFechaInicio() == null) {
                throw new Exception("Fecha de inicio necesaria");
            }
            if (prestamoDTO.getMeses() == null) {
                throw new Exception("Cantidad de meses necesaria");
            }
            LocalDate fechaInicio = prestamoDTO.getFechaInicio();
            int meses = prestamoDTO.getMeses();
            Prestamo prestamo = mapper.convertirAEntidad(prestamoDTO, Prestamo.class);
            prestamo.setId(null);
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

    @Transactional()
    public ApiResponse<PrestamoDTO> update(PrestamoDTO prestamoDTO) {
        LocalDate fechaInicio = prestamoDTO.getFechaInicio();
        int meses = prestamoDTO.getMeses();
        try {
            Prestamo prestamo = mapper.convertirAEntidad(prestamoDTO, Prestamo.class);
            if (!exist(prestamoDTO.getId())) {
                return ApiResponse.error("No existe prestamo a actualizar");
            }
            Prestamo prestamoDB = prestamoRepository.findById(prestamoDTO.getId()).orElse(null);
            List<Pago> pagosRealizados = pagoRepository.findByPrestamoIdAndMontoGreaterThan(prestamoDTO.getId(),
                    BigDecimal.ZERO);
            if (!prestamoDTO.getFechaInicio().isEqual(prestamoDB.getFechaInicio())) {
                return ApiResponse.error("No se puede cambiar la fecha de inicio ");
            }
            if (prestamoDTO.getMeses() < pagosRealizados.size()) {
                return ApiResponse.error("No se puede reducir el número de meses, tiene pagos realizados ");
            }
            if (prestamoDTO.getMeses() < prestamoDB.getMeses()) {
                return ApiResponse.error("No se puede reducir el número de meses ");
            }
            // se generan automaticamente los pagos
            fechaInicio = pagoRepository.findFirstByPrestamoIdOrderByFechaVencimientoDesc(prestamoDTO.getId()).get()
                    .getFechaVencimiento();
            if (fechaInicio == null) {
                return ApiResponse.error("No encontramos la ultima fecha de pago ");
            }
            // si aumenta los meses se generaran mas pagos
            meses = prestamoDTO.getMeses() - prestamoDB.getMeses();
            prestamo.setFechaLimite(fechaInicio.plusMonths(meses));
            Prestamo newPrestamo = prestamoRepository.save(prestamo);
            pagoService.generarPagos(newPrestamo, fechaInicio, meses);
            return ApiResponse.success("Prestamo actualizado", mapper.convertirADTO(newPrestamo, PrestamoDTO.class));

        } catch (Exception e) {
            System.out.println("Error al actualizar el prestamo: " + e.getMessage());
            return ApiResponse.error("Error interno del servidor: ");
        }
    }

    public ApiResponse<Boolean> delete(int id) {
        try {
            Prestamo prestamo = prestamoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe prestamo con id: " + id));

            // si tiene pagos, eliminarlos primero
            if (prestamo.getPagos() != null && !prestamo.getPagos().isEmpty()) {
                pagoRepository.deleteAll(prestamo.getPagos());
            }
            prestamoRepository.delete(prestamo);

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

}