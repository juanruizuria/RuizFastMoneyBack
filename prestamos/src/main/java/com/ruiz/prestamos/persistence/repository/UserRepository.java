package com.ruiz.prestamos.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ruiz.prestamos.persistence.entity.Usuario;

public interface UserRepository extends CrudRepository<Usuario, Integer> {
    Optional<Usuario> findByNombre(String nombre);
    Usuario findByCorreo(String correo);
    Usuario findByNombreAndContrasenia(String nombre, String contrasenia);
}


