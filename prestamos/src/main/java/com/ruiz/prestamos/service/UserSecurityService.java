package com.ruiz.prestamos.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ruiz.prestamos.persistence.entity.Usuario;
import com.ruiz.prestamos.persistence.entity.UsuarioRol;
import com.ruiz.prestamos.persistence.repository.UserRepository;

@Service
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;

    public UserSecurityService(UserRepository userRepository,  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Implementación del método loadUserByUsername para cargar el usuario por nombre de usuario
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario userEntity = userRepository.findByNombre(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        String[] roles = userEntity.getRoles().stream()
                .map(role -> role.getRol())
                .toArray(String[]::new); 
        
        return User.builder()
                .username(userEntity.getNombre())
                .password(userEntity.getContrasenia())
                .roles(roles) 
                //.authorities(grantedAuthorities(roles))
                .accountLocked(userEntity.getBloqueada())
                .disabled(userEntity.getDeshabilitada())
                .build();
    }

    public Usuario createUser(String username, String rawPassword) {
        if (userRepository.findByNombre(username).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        Usuario newUser = new Usuario();
        newUser.setNombre(username);
        newUser.setContrasenia(passwordEncoder.encode(rawPassword));
        newUser.setBloqueada(false);
        newUser.setDeshabilitada(false);

        UsuarioRol defaultRole = new UsuarioRol();
        defaultRole.setRol("USER");
        newUser.setRoles(Collections.singletonList(defaultRole));

        return userRepository.save(newUser);
    }

    private String[] getAuthorities(String role) {
        if ("admin".equals(role) || "customer".equals(role)) {
               return new String[]{"random_order"};
        }
        return new String[]{};
    }

    private List<GrantedAuthority> grantedAuthorities(String[] roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            for (String authority : this.getAuthorities(role)) {
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        return authorities;
    }
    
}

