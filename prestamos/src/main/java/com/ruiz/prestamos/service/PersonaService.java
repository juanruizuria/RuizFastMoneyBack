package com.ruiz.prestamos.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ruiz.prestamos.controller.ApiResponse;
import com.ruiz.prestamos.persistence.dto.PersonaDTO;
import com.ruiz.prestamos.persistence.entity.Persona;
import com.ruiz.prestamos.persistence.repository.PersonaRepository;

@Service
public class PersonaService {
    private final PersonaRepository personaRepository;
    private ModelMapper modelMapper;

    public PersonaService(PersonaRepository personaRepository, ModelMapper modelMapper) {
        this.personaRepository = personaRepository;
        this.modelMapper = modelMapper;
    }

    public ApiResponse<List<PersonaDTO>> getAll() {
        try {
            List<Persona> personas = personaRepository.findAll();
            if(personas.isEmpty()) {
                throw new NoSuchElementException("No existen personas");
            }
            return ApiResponse.success("personas encontradas", convertirListaADTO(personas));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning("No existen personas", null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<PersonaDTO> get(int id) {
        try {
            Persona persona = personaRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe persona con id: " + id));
            return ApiResponse.success("persona encontrada", convertirADTO(persona));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<PersonaDTO> save(Persona inputPersona) {
        try {
            Persona persona = personaRepository.save(inputPersona);
            return ApiResponse.success("Persona guardada", convertirADTO(persona));
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<PersonaDTO> update(Persona inputPersona) {
        try {
            if(!exist(inputPersona.getId())) {
                throw new NoSuchElementException("No existe persona con id: " + inputPersona.getId());
            }
            Persona persona = personaRepository.save(inputPersona);
            return ApiResponse.success("Persona modificada", convertirADTO(persona));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<PersonaDTO> delete(int id) {
        try {
            Persona persona = personaRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe persona con id: " + id));
            personaRepository.deleteById(id);
            return ApiResponse.success("persona eliminada", convertirADTO(persona));

        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public boolean exist(Integer id) {
        boolean result = false;
        if (id != null && personaRepository.existsById(id)) {
            result = true;
        }
        return result;
    }

    public PersonaDTO convertirADTO(Persona persona) {
        return modelMapper.map(persona, PersonaDTO.class);
    }

    public Persona convertirAEntidad(PersonaDTO personaDTO) {
        return modelMapper.map(personaDTO, Persona.class);
    }

    public List<PersonaDTO> convertirListaADTO(List<Persona> personas) {
        return personas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

}
