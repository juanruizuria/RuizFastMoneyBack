package com.ruiz.prestamos.persistence.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario_roles")
@IdClass(UserRoleId.class)
@Getter
@Setter
@NoArgsConstructor
public class UsuarioRol {
    @Id
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Id
    @Column(nullable = false)
    private String rol;

    @Column(name = "fecha_inicio", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDate fechaInicio;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)  
    private Usuario usuario;

}
