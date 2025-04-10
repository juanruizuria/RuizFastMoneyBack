package com.ruiz.prestamos.config;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenericMapper {
     @Autowired
    private ModelMapper modelMapper;

    public <D, E> D convertirADTO(E entidad, Class<D> dtoClass) {
        return modelMapper.map(entidad, dtoClass);
    }

    public <D, E> E convertirAEntidad(D dto, Class<E> entidadClass) {
        return modelMapper.map(dto, entidadClass);
    }

    public <D, E> List<D> convertirListaADTO(List<E> entidades, Class<D> dtoClass) {
        return entidades.stream()
                .map(entidad -> convertirADTO(entidad, dtoClass))
                .collect(Collectors.toList());
    }
}
