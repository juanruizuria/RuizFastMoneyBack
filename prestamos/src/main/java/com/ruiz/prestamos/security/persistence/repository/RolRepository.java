package com.ruiz.prestamos.security.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;

import com.ruiz.prestamos.security.persistence.entity.Rol;

public interface RolRepository extends ListCrudRepository<Rol, Integer>  {
    Optional<Rol> findByNombre(String nombre);
}
