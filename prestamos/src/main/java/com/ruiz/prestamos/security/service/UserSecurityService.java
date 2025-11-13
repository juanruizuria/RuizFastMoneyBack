package com.ruiz.prestamos.security.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
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

import com.ruiz.prestamos.config.GenericMapper;
import com.ruiz.prestamos.security.persistence.dto.RolDTO;
import com.ruiz.prestamos.security.persistence.dto.UsuarioDTO;
import com.ruiz.prestamos.security.persistence.entity.Rol;
import com.ruiz.prestamos.security.persistence.entity.Usuario;
import com.ruiz.prestamos.security.persistence.repository.UserRepository;
import com.ruiz.prestamos.util.ApiResponse;

@Service
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolService rolService;
    private GenericMapper mapper;

    public UserSecurityService(UserRepository userRepository, PasswordEncoder passwordEncoder, RolService rolService,
            GenericMapper mapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolService = rolService;
        this.mapper = mapper;
    }

    public ApiResponse<List<UsuarioDTO>> listarUsuarios() {
        try {
            List<Usuario> usuarios = userRepository.findAll();
            if (usuarios.isEmpty()) {
                throw new NoSuchElementException("No existen usuarios");
            }
            List<UsuarioDTO> dtos = mapper.convertirListaADTO(usuarios, UsuarioDTO.class);
            List<UsuarioDTO> resultado = usuarios.stream()
                    .map(usuario -> {
                        UsuarioDTO dto = dtos.get(usuarios.indexOf(usuario));

                        usuario.getRoles().stream().findFirst().ifPresent(rol -> {
                            RolDTO rolDTO = new RolDTO();
                            rolDTO.setId(rol.getId());
                            rolDTO.setNombre(rol.getNombre());
                            dto.setRol(rolDTO);
                        });

                        return dto;
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("usuarios encontrados", resultado);
        } catch (NoSuchElementException e) {
            return ApiResponse.warning("No existen usuarios", null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
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

    public Usuario getById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe usuario con id: " + id));
    }

    @Transactional
    public ApiResponse<UsuarioDTO> createUser(UsuarioDTO dto) {
        try {
            // Validar si el usuario ya existe
            if (userRepository.findByUsuario(dto.getUsuario()).isPresent()) {
                throw new IllegalArgumentException("El usuario ya existe");
            }

            Usuario newUser = mapper.convertirAEntidad(dto, Usuario.class);

            // Encriptar contraseña antes de guardar
            if (dto.getContrasenia() != null && !dto.getContrasenia().isBlank()) {
                newUser.setContrasenia(passwordEncoder.encode(dto.getContrasenia()));
            } else {
                throw new IllegalArgumentException("La contraseña es obligatoria");
            }

            // Asignar rol (si se envió)
            if (dto.getRol() != null && dto.getRol().getId() != null) {
                Rol rolAsignado = rolService.getById(dto.getRol().getId());
                newUser.setRoles(Set.of(rolAsignado));
            }

            // Guardar y retornar
            userRepository.save(newUser);
            return ApiResponse.success("Usuario guardado", mapper.convertirADTO(newUser, UsuarioDTO.class));

        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<UsuarioDTO> actualizarUsuario(UsuarioDTO dto) {
        try {
            Usuario usuario = userRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (dto.getUsuario() != null) {
                usuario.setUsuario(dto.getUsuario());
            }
            if (dto.getNombre() != null) {
                usuario.setNombre(dto.getNombre());
            }
            if (dto.getCorreo() != null) {
                usuario.setCorreo(dto.getCorreo());
            }
            if (dto.getBloqueada() != null) {
                usuario.setBloqueada(dto.getBloqueada());
            }
            if (dto.getDeshabilitada() != null) {
                usuario.setDeshabilitada(dto.getDeshabilitada());
            }

            if (dto.getContrasenia() != null && !dto.getContrasenia().isBlank()) {
                usuario.setContrasenia(passwordEncoder.encode(dto.getContrasenia()));
            }

            // Actualizar rol (solo uno)
            if (dto.getRol() != null && dto.getRol().getId() != null) {
                Rol nuevoRol = rolService.getById(dto.getRol().getId());
                usuario.setRoles(new HashSet<>(Collections.singleton(nuevoRol)));
            } else {
                throw new NoSuchElementException("Debe seleccionar un rol");
            }

            Usuario actualizado = userRepository.save(usuario);

            return ApiResponse.success("Usuario modificado", mapper.convertirADTO(actualizado, UsuarioDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<UsuarioDTO> deshabilitarUsuario(Integer id) {
        try {
            Usuario usuario = userRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

            usuario.setDeshabilitada(true);
            userRepository.save(usuario);

            return ApiResponse.success("Usuario deshabilitado", mapper.convertirADTO(usuario, UsuarioDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<Usuario> eliminarUsuario(Integer id) {
        try {
            Usuario usuario = userRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

            userRepository.delete(usuario);
            return ApiResponse.success("Usuario eliminado", usuario);
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

}
