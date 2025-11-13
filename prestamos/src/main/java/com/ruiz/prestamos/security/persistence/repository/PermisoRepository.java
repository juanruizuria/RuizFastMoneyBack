package com.ruiz.prestamos.security.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ruiz.prestamos.security.persistence.entity.Permiso;

@Repository
public interface PermisoRepository extends ListCrudRepository<Permiso, Integer> {

    Optional<Permiso> findByNombre(String nombre);

    @Modifying
    @Query(value = "DELETE FROM rol_permisos WHERE id_permiso = :id", nativeQuery = true)
    void deleteRolPermisosByPermisoId(@Param("id") int id);

}
