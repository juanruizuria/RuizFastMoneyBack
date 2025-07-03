package com.ruiz.prestamos.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruiz.prestamos.config.JWTUtil;
import com.ruiz.prestamos.service.UserSecurityService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserSecurityService userSecurityService;
    private JWTUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,UserSecurityService userSecurityService, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userSecurityService = userSecurityService;
    }

    @PostMapping("/loginHeader")
    public ResponseEntity<Void> loginHeader(@RequestBody LoginDto req) {
        try {
            UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
            Authentication authentication = this.authenticationManager.authenticate(login);
            System.out.println("esta autenticado?: " + authentication.isAuthenticated());
            System.out.println("Usuario autenticado: " + authentication.getPrincipal());
            String jwt = jwtUtil.generarToken(req.getUsername());
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwt).build();
        } catch (BadCredentialsException e) {
            System.out.println("Error al login:" + e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto req) {
        try {
            UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(req.getUsername(),req.getPassword());
            Authentication authentication = this.authenticationManager.authenticate(login);
            UserDetails userDetails = userSecurityService.loadUserByUsername(req.getUsername());
            String jwt = jwtUtil.generarToken(req.getUsername());
            LoginResponseDto responseDto = new LoginResponseDto(userDetails, jwt);
            System.out.println("Usuario autenticado: " + authentication.getPrincipal());
            return ResponseEntity.ok(responseDto);
        } catch (BadCredentialsException e) {
            System.out.println("Error en login:" + e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
