package com.ruiz.prestamos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruiz.prestamos.config.GenericMapper;
import com.ruiz.prestamos.persistence.dto.RolDTO;
import com.ruiz.prestamos.persistence.entity.Permiso;
import com.ruiz.prestamos.persistence.entity.Rol;
import com.ruiz.prestamos.persistence.repository.RolRepository;
import com.ruiz.prestamos.util.ApiResponse;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RolService {

    private final RolRepository rolRepository;
    private final PermisoService permisoService;
    private GenericMapper mapper;

    public RolService(RolRepository rolRepository, PermisoService permisoService, GenericMapper mapper) {
        this.rolRepository = rolRepository;
        this.permisoService = permisoService;
        this.mapper = mapper;

    }

    public ApiResponse<List<RolDTO>> listarRoles() {
        try {
            List<Rol> roles = rolRepository.findAll();

            if (roles.isEmpty()) {
                return ApiResponse.warning("No existen registros", null);
            }

            List<RolDTO> rolDTOs = roles.stream()
                    .map(rol -> {
                        RolDTO dto = mapper.convertirADTO(rol, RolDTO.class);
                        Set<Integer> permisosIds = rol.getPermisos() == null
                                ? Collections.emptySet()
                                : rol.getPermisos().stream()
                                        .map(Permiso::getId)
                                        .collect(Collectors.toSet());
                        dto.setPermisosIds(permisosIds);
                        return dto;
                    }).collect(Collectors.toList());

            return ApiResponse.success("Roles encontrados", rolDTOs);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<Set<Integer>> permisosByRol(Integer rolId) {
        try {
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new NoSuchElementException("No existe rol con id: " + rolId));
            Set<Integer> permisosIds = rol.getPermisos().stream().map(p -> p.getId()).collect(Collectors.toSet());
            return ApiResponse.success("permisos encontrados", permisosIds);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public Rol getById(int id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe rol con id: " + id));
    }

    public Rol obtenerPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombre));
    }

    @Transactional
    public ApiResponse<RolDTO> save(RolDTO input) {
        try {
            if (rolRepository.findByNombre(input.getNombre()).isPresent()) {
                throw new IllegalArgumentException("El rol ya existe");
            }
            Rol rol = new Rol();
            rol.setNombre(input.getNombre());
            if (input.getPermisosIds() != null) {
                Set<Permiso> permisos = new HashSet<>(permisoService.findAllById(input.getPermisosIds()));
                rol.setPermisos(permisos);
            }
            Rol newRol = rolRepository.save(rol);
            return ApiResponse.success("Rol guardado", mapper.convertirADTO(newRol, RolDTO.class));
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<Rol> delete(int id) {
        try {
            Rol rol = rolRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe rol con id: " + id));
            rolRepository.deleteById(id);
            return ApiResponse.success("Rol eliminado", rol);

        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<RolDTO> update(RolDTO input) {
        try {
            Rol rol = rolRepository.findById(input.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
            rol.setNombre(input.getNombre().toUpperCase());
            // Actualiza permisos
            if (input.getPermisosIds() != null) {
                Set<Permiso> permisos = new HashSet<>(permisoService.findAllById(input.getPermisosIds()));
                rol.setPermisos(permisos);
            }

            Rol uRol = rolRepository.save(rol);
            return ApiResponse.success("Rol modificado", mapper.convertirADTO(uRol, RolDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }
}
