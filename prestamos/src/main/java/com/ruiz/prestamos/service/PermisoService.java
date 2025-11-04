package com.ruiz.prestamos.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruiz.prestamos.persistence.dto.PermisoDTO;
import com.ruiz.prestamos.persistence.entity.Permiso;
import com.ruiz.prestamos.persistence.repository.PermisoRepository;
import com.ruiz.prestamos.util.ApiResponse;

@Service
public class PermisoService {

    private final PermisoRepository repository;

    public PermisoService(PermisoRepository permisoRepository) {
        this.repository = permisoRepository;
    }

    public ApiResponse<List<PermisoDTO>> listarPermisos() {
        List<PermisoDTO> permisos = repository.findAll().stream()
                .map(p -> new PermisoDTO(
                        p.getId(),
                        p.getNombre(),
                        p.getLabel(),
                        p.getIcon(),
                        p.getRoute(),
                        p.getParent() != null ? p.getParent().getId() : null))
                .toList();

        if (permisos.isEmpty()) {
            return ApiResponse.warning("No existen registros", null);
        }

        return ApiResponse.success("Permisos encontrados", permisos);
    }

    public Permiso getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe Permiso con id: " + id));
    }

    public Permiso obtenerPorNombre(String nombre) {
        return repository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado: " + nombre));
    }

    @Transactional
    public ApiResponse<PermisoDTO> save(PermisoDTO dto) {
        try {
            if (repository.findByNombre(dto.getNombre()).isPresent()) {
                throw new IllegalArgumentException("El Permiso ya existe");
            }
            Permiso permiso = new Permiso();
            permiso.setNombre(dto.getNombre());
            permiso.setLabel(dto.getLabel());
            permiso.setIcon(dto.getIcon());
            permiso.setRoute(dto.getRoute());
            if (dto.getParentId() != null) {
                Permiso parent = repository.findById(dto.getParentId())
                        .orElseThrow(() -> new RuntimeException("Parent not found"));
                permiso.setParent(parent);
            }
            repository.save(permiso);
            return ApiResponse.success("Permiso guardado", null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<PermisoDTO> update(PermisoDTO dto) {
        try {
            Permiso permiso = repository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado"));
            permiso.setNombre(dto.getNombre().toUpperCase());
            permiso.setLabel(dto.getLabel());
            permiso.setIcon(dto.getIcon());
            permiso.setRoute(dto.getRoute());
            if (dto.getParentId() != null) {
                Permiso parent = repository.findById(dto.getParentId())
                        .orElseThrow(() -> new IllegalArgumentException("Permiso padre no encontrado"));
                permiso.setParent(parent);
            } else {
                permiso.setParent(null);
            }

            repository.save(permiso);

            return ApiResponse.success("Permiso modificado", null);
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<Permiso> delete(int id) {
        try {
            Permiso Permiso = repository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe Permiso con id: " + id));
            // Eliminar relaciones en la tabla intermedia antes de eliminar el permiso
            repository.deleteRolPermisosByPermisoId(id);
            repository.deleteById(id);
            return ApiResponse.success("Permiso eliminado", Permiso);

        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public List<Permiso> findAllById(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return repository.findAllById(ids);
    }

}
