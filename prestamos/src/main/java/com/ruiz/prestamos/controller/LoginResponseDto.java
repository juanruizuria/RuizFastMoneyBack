package com.ruiz.prestamos.controller;

import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class LoginResponseDto {
    private UserDetails user;
    private String token;

    public LoginResponseDto(UserDetails user, String token) {
        this.user = user;
        this.token = token;
    }
}
