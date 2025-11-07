package com.ruiz.prestamos.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;

import com.ruiz.prestamos.persistence.entity.Usuario;

public interface UserRepository extends ListCrudRepository<Usuario, Integer> {
    Optional<Usuario> findByUsuario(String usuario);
    Usuario findByCorreo(String correo);
    Usuario findByNombreAndContrasenia(String nombre, String contrasenia);
}


