package com.ruiz.prestamos.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ruiz.prestamos.persistence.entity.Rol;
import com.ruiz.prestamos.persistence.entity.Usuario;
import com.ruiz.prestamos.persistence.repository.UserRepository;

@Service
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolService rolService;

    public UserSecurityService(UserRepository userRepository, PasswordEncoder passwordEncoder, RolService rolService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolService = rolService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario userEntity = userRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario: " + username));

        // Obtener authorities: roles + permisos
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Rol rol : userEntity.getRoles()) {
            // Agregar el rol con prefijo "ROLE_"
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));

            // Agregar todos los permisos de ese rol
            if (rol.getPermisos() != null) {
                rol.getPermisos().forEach(permiso -> authorities.add(new SimpleGrantedAuthority(permiso.getNombre())));
            }
        }

        return User.builder()
                .username(userEntity.getUsuario())
                .password(userEntity.getContrasenia())
                .authorities(authorities)
                .accountLocked(userEntity.getBloqueada())
                .disabled(userEntity.getDeshabilitada())
                .build();
    }

    @Transactional
    public Usuario createUser(String usuario, String nombre, String rawPassword, List<String> nombresRoles) {
        // Validar si el usuario ya existe
        if (userRepository.findByUsuario(usuario).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        // Si no se pasan roles, usar USER por defecto
        if (nombresRoles == null || nombresRoles.isEmpty()) {
            nombresRoles = List.of("USER");
        } else if (nombresRoles.stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN")) &&
                nombresRoles.stream().noneMatch(r -> r.equalsIgnoreCase("USER"))) {
            nombresRoles = new ArrayList<>(nombresRoles);
            nombresRoles.add("USER");
        }

        // Crear el usuario
        Usuario newUser = new Usuario();
        newUser.setUsuario(usuario);
        newUser.setCorreo(nombre); // o setNombreCompleto si existe
        newUser.setContrasenia(passwordEncoder.encode(rawPassword));
        newUser.setBloqueada(false);
        newUser.setDeshabilitada(false);

        // Asignar roles
        Set<Rol> rolesAsignados = nombresRoles.stream()
                .map(nombreRol -> rolService.obtenerPorNombre(nombreRol.toUpperCase()))
                .collect(Collectors.toSet());

        newUser.setRoles(rolesAsignados);

        // Guardar y retornar
        return userRepository.save(newUser);
    }

}
