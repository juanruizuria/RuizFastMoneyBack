package com.ruiz.prestamos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruiz.prestamos.config.JWTUtil;
import com.ruiz.prestamos.persistence.dto.LoginDTO;
import com.ruiz.prestamos.persistence.dto.LoginResponseDTO;
import com.ruiz.prestamos.persistence.dto.MenuDTO;
import com.ruiz.prestamos.service.MenuService;
import com.ruiz.prestamos.service.UserSecurityService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserSecurityService userSecurityService;
    private final MenuService menuService;
    private JWTUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserSecurityService userSecurityService,
            JWTUtil jwtUtil, MenuService menuService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userSecurityService = userSecurityService;
        this.menuService = menuService;
    }

    @GetMapping("/menu")
    public ResponseEntity<List<MenuDTO>> getMenu(@RequestHeader("Authorization") String header) {
        String token = jwtUtil.getToken(header);
        if (token == null || !jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtUtil.getUsername(token);
        List<MenuDTO> menus = menuService.getMenuByUsuario(username);
        System.out.println("Menu para usuario " + username + ": " + menus);
        return ResponseEntity.ok(menus);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO req) {
        try {
            UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(req.getUsername(),
                    req.getPassword());
            Authentication authentication = this.authenticationManager.authenticate(login);
            UserDetails userDetails = userSecurityService.loadUserByUsername(req.getUsername());
            // Extraer roles y permisos
            List<String> authorities = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            List<String> roles = authorities.stream()
                    .filter(a -> a.startsWith("ROLE_"))
                    .toList();

            List<String> permisos = authorities.stream()
                    .filter(a -> !a.startsWith("ROLE_"))
                    .toList();

            String jwt = jwtUtil.generarToken(req.getUsername());
            LoginResponseDTO responseDto = new LoginResponseDTO(userDetails, jwt);
            responseDto.setRoles(roles);
            responseDto.setPermisos(permisos);
            System.out.println("Usuario autenticado: " + authentication.getPrincipal());
            return ResponseEntity.ok(responseDto);
        } catch (BadCredentialsException e) {
            System.out.println("Error en login:" + e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<LoginResponseDTO> getAuthStatus(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = jwtUtil.generarToken(userDetails.getUsername());
        if (token == null || !jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        List<String> roles = authorities.stream()
                .filter(a -> a.startsWith("ROLE_"))
                .toList();

        List<String> permisos = authorities.stream()
                .filter(a -> !a.startsWith("ROLE_"))
                .toList();
        LoginResponseDTO responseDto = new LoginResponseDTO(userDetails, token);
        responseDto.setRoles(roles);
        responseDto.setPermisos(permisos);

        return ResponseEntity.ok(responseDto);
    }

}
