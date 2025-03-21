package com.ruiz.prestamos.persistence.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
public class Auditable {
    @Column(name = "fecha_creacion")
    @CreatedDate
    private LocalDateTime fechaCreacion;


    @Column (name = "fecha_modificacion")
    @LastModifiedDate
    private LocalDateTime fechaModificacion;

}
