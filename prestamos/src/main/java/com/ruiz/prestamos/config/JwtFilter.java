package com.ruiz.prestamos.config;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService; //usara UserSecurityService pq implementa


    public JwtFilter(JWTUtil jwtUtil, @Lazy UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Validar que sea un Header de autorización valido
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {
            System.out.println("Invalid Authorization Header");
            // que la cadena de filtros continúe su trabajo. es una peticion que no necesita
            // seguridad
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Validar que el token sea valido (Bearer ERWQEq32.fdfsdf.324432)
        String token = authHeader.split(" ")[1].trim();
        if(!jwtUtil.validarToken(token)){
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Cargar el usuario del UserDetailsService y controlar que exista en la bd
        String userName = jwtUtil.getUsername(token);
        User user = (User) userDetailsService.loadUserByUsername(userName); //busca en bd
        
        // 4. Cargar el usuario en el contexto de seguridad
        
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //cargamos los detalles de la autenticacion
        SecurityContextHolder.getContext().setAuthentication(authToken); //cargamos la autenticacion en el contexto
        System.out.println(authToken);
        filterChain.doFilter(request, response);

    }
}
