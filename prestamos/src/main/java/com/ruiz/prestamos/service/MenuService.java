package com.ruiz.prestamos.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ruiz.prestamos.persistence.dto.MenuDTO;
import com.ruiz.prestamos.persistence.entity.Permiso;
import com.ruiz.prestamos.persistence.entity.Usuario;
import com.ruiz.prestamos.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final UserRepository userRepository;

     public List<MenuDTO> getMenuByUsuario(String username) {
        Usuario usuario = userRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Todos los permisos únicos del usuario
        Set<Permiso> permisos = usuario.getRoles().stream()
                .flatMap(rol -> rol.getPermisos().stream())
                .collect(Collectors.toSet());

        // Solo los permisos raíz (sin parent)
        List<Permiso> padres = permisos.stream()
                .filter(p -> p.getParent() == null)
                .sorted(Comparator.comparing(Permiso::getLabel))
                .toList();

        // Construir menú recursivo
        return padres.stream()
                .map(p -> mapToDTO(p, permisos))
                .toList();
    }

    private MenuDTO mapToDTO(Permiso permiso, Set<Permiso> todos) {
        // Hijos visibles del usuario (que también estén en su conjunto de permisos)
        List<MenuDTO> hijos = todos.stream()
                .filter(p -> permiso.equals(p.getParent()))
                .sorted(Comparator.comparing(Permiso::getLabel))
                .map(p -> mapToDTO(p, todos))
                .toList();

        return new MenuDTO(
                permiso.getLabel(),
                permiso.getIcon(),
                permiso.getRoute(),
                hijos
        );
    }
}
