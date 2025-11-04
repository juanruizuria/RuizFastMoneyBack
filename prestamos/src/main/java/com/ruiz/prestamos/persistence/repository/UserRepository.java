package com.ruiz.prestamos.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ruiz.prestamos.persistence.entity.Usuario;

public interface UserRepository extends CrudRepository<Usuario, Integer> {
    Optional<Usuario> findByUsuario(String usuario);
    Usuario findByCorreo(String correo);
    Usuario findByNombreAndContrasenia(String nombre, String contrasenia);
}


