package com.ruiz.prestamos.security.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Rol implements GrantedAuthority {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false, unique = true, length = 50)
   private String nombre;

   @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rol_permisos",
        joinColumns = @JoinColumn(name = "id_rol"),
        inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    private Set<Permiso> permisos = new HashSet<>();

   @Override
   public String getAuthority() {
      return nombre;
   }
}
