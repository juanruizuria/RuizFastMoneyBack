package com.ruiz.prestamos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruiz.prestamos.config.ModelMapperConfig;
import com.ruiz.prestamos.controller.PagoController;
import com.ruiz.prestamos.persistence.dto.PrestamoDTO;
import com.ruiz.prestamos.persistence.entity.Pago;
import com.ruiz.prestamos.persistence.entity.Prestamo;
import com.ruiz.prestamos.persistence.enums.EstadoPago;
import com.ruiz.prestamos.persistence.repository.PagoRepository;
import com.ruiz.prestamos.persistence.repository.PrestamoRepository;

@Service
public class PrestamoService {   
    private final PrestamoRepository prestamoRepository;
    private final PagoRepository pagoRepository;
    private ModelMapper modelMapper;

    public PrestamoService(PrestamoRepository prestamoRepository, PagoRepository pagoRepository,
            ModelMapper modelMapper, ModelMapperConfig modelMapperConfig, PagoController pagoController) {
        this.prestamoRepository = prestamoRepository;
        this.pagoRepository = pagoRepository;
        this.modelMapper = modelMapper;

    }

    public List<Prestamo> getAll() {
        return prestamoRepository.findAll();
    }

    public Prestamo get(int id) {
        return prestamoRepository.findById(id).orElse(null);
    }

    @Transactional()
    public Prestamo save(Prestamo prestamo) {
        int meses = prestamo.getMeses();
        LocalDate fechaInicio = prestamo.getFechaInicio();
        try {
            if (exist(prestamo.getId())) {// update
                Prestamo prestamoDB = prestamoRepository.findById(prestamo.getId()).orElse(null);
                List<Pago> pagosRealizados = pagoRepository.findByPrestamoIdAndMontoGreaterThan(prestamo.getId(),BigDecimal.ZERO);
                if(prestamo.getMeses() < pagosRealizados.size()){
                    throw new Exception("No se puede reducir el número de meses, tiene pagos realizados");
                }
                if(prestamo.getMeses() < prestamoDB.getMeses()){
                    throw new Exception("No se puede reducir el número de meses");
                }
                fechaInicio = pagoRepository.findFirstByPrestamoIdOrderByFechaPagoDesc(prestamo.getId()).get().getFechaPago();  
                meses = prestamo.getMeses() - prestamoDB.getMeses();
            } 
            for (int i = 1; i <= meses; i++) {
                Pago pago = new Pago();
                pago.setPrestamo(prestamo);
                pago.setEstado(EstadoPago.PENDIENTE);
                pago.setFechaVencimiento(fechaInicio.plusMonths(i));
                pagoRepository.save(pago);
            }          

        } catch (Exception e) {
            System.out.println("Error al guardar el prestamo"+e.getMessage());
            return null;
        }
        return prestamoRepository.save(prestamo);
    }

    public boolean delete(int id) {
        try {
            prestamoRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean exist(Integer id) {
        boolean result = false;
        if (id != null && prestamoRepository.existsById(id)) {
            result = true;
        }
        return result;
    }

    public PrestamoDTO convertirADTO(Prestamo prestamo) {
        return modelMapper.map(prestamo, PrestamoDTO.class);
    }

    public Prestamo convertirAEntidad(PrestamoDTO prestamoDTO) {
        return modelMapper.map(prestamoDTO, Prestamo.class);
    }

    public List<PrestamoDTO> convertirListaADTO(List<Prestamo> prestamos) {
        return prestamos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<PrestamoDTO> obtenerTodos() {
        List<Prestamo> Prestamos = prestamoRepository.findAll();
        return convertirListaADTO(Prestamos);
    }

}
