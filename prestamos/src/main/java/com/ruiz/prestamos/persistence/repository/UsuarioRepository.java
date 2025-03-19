package com.ruiz.prestamos.persistence.repository;

import org.springframework.data.repository.ListCrudRepository;
import com.ruiz.prestamos.persistence.entity.Usuario;

public interface UsuarioRepository extends ListCrudRepository<Usuario,Integer>{

}


