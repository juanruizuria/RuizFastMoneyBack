package com.ruiz.prestamos.persistence.dto;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private UserDetails user;
    private String token;
    private List<String> roles;
    private List<String> permisos;

    public LoginResponseDTO(UserDetails user, String token) {
        this.user = user;
        this.token = token;
    }
}
