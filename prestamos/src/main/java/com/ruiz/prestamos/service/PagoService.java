package com.ruiz.prestamos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ruiz.prestamos.persistence.dto.PagoDTO;
import com.ruiz.prestamos.persistence.entity.Pago;
import com.ruiz.prestamos.persistence.repository.PagoRepository;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;
    private ModelMapper modelMapper;

    public PagoService(PagoRepository pagoRepository, ModelMapper modelMapper) {
        this.pagoRepository = pagoRepository;
        this.modelMapper = modelMapper;
    }

    public List<Pago> getAll() {
        return pagoRepository.findAll();
    }

    public Pago get(int id) {
        return pagoRepository.findById(id).orElse(null);
    }

    public Pago save(Pago pago) {
        return pagoRepository.save(pago);
    }

    public boolean delete(int id) {
        try {
            pagoRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean exist(Integer id) {
        boolean result = false;
        if (id != null && pagoRepository.existsById(id)) {
            result = true;
        }
        return result;
    }

    public PagoDTO convertirADTO(Pago pago) {
        return modelMapper.map(pago, PagoDTO.class);
    }

    public Pago convertirAEntidad(PagoDTO pagoDTO) {
        return modelMapper.map(pagoDTO, Pago.class);
    }

    public List<PagoDTO> convertirListaADTO(List<Pago> pagos) {
        return pagos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<PagoDTO> obtenerTodos() {
        List<Pago> pagos = pagoRepository.findAll();
        return convertirListaADTO(pagos);
    }

}
