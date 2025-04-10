package com.ruiz.prestamos.persistence.audit;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
public class Auditable {
    @Column(name = "fecha_creacion")
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    @LastModifiedDate
    private LocalDateTime fechaModificacion;

    @Column(name = "creado_por")
    @CreatedBy
    private String createdBy;

    @Column(name = "modificado_por")
    @LastModifiedBy
    private String modifiedBy;

}
