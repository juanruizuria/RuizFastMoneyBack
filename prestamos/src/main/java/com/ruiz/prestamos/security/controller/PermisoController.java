package com.ruiz.prestamos.security.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruiz.prestamos.security.persistence.dto.PermisoDTO;
import com.ruiz.prestamos.security.persistence.entity.Permiso;
import com.ruiz.prestamos.security.service.PermisoService;
import com.ruiz.prestamos.util.ApiResponse;

@RestController
@RequestMapping("/api/permiso")
public class PermisoController {
    private final PermisoService service;

    public PermisoController(PermisoService permisoService) {
        this.service = permisoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermisoDTO>>> getAll() {
        return ResponseEntity.ok(this.service.listarPermisos());
    }

    @GetMapping("/{idPermiso}")
    public ResponseEntity<Permiso> get(@PathVariable("idPermiso") int id) {
        return ResponseEntity.ok(this.service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PermisoDTO>> add(@RequestBody PermisoDTO  input) {
        input.setId(null);
        return ResponseEntity.ok(this.service.save(input));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PermisoDTO>> update(@RequestBody PermisoDTO  intput) {
        return ResponseEntity.ok(this.service.update(intput));
    }

    @DeleteMapping("/{idPermiso}")
    public ResponseEntity<ApiResponse<Permiso>> delete(@PathVariable("idPermiso") int id) {
        return ResponseEntity.ok(this.service.delete(id));

    }

}
