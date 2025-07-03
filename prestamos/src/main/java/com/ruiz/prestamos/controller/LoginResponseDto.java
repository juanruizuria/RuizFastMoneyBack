package com.ruiz.prestamos.controller;

import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class LoginResponseDto {
    private UserDetails username;
    private String token;

    public LoginResponseDto(UserDetails username, String token) {
        this.username = username;
        this.token = token;
    }
}
