package com.ruiz.prestamos.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ruiz.prestamos.persistence.entity.Usuario;
import com.ruiz.prestamos.persistence.repository.UserRepository;

@Service
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

