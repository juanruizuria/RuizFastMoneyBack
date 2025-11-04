package com.ruiz.prestamos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                //.httpBasic(Customizer.withDefaults()) // habilitar autenticación básica
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.POST, "/api/auth/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/imagen/**").permitAll() 
                        .requestMatchers(HttpMethod.POST, "/api/imagen/**").authenticated()
                        //.requestMatchers(HttpMethod.GET, "/api/persona/**").hasAnyRole("ADMIN","CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/persona/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/persona/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/persona/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/permisos/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/permisos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/permisos/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/rol/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/rol/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/rol/**").hasRole("ADMIN")

                        


                        
                        /*
                         * .requestMatchers("/prestamos/**").hasAnyRole("ADMIN", "USER")
                         * .requestMatchers("/swagger-ui/**").permitAll()
                         * .requestMatchers("/public/**").permitAll()
                         */

                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
