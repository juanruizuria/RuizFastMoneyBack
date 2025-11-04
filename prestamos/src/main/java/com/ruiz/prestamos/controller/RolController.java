package com.ruiz.prestamos.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruiz.prestamos.persistence.dto.RolDTO;
import com.ruiz.prestamos.persistence.entity.Rol;
import com.ruiz.prestamos.service.RolService;
import com.ruiz.prestamos.util.ApiResponse;

@RestController
@RequestMapping("/api/rol")
public class RolController {
    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;

    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RolDTO>>> listarRoles() {
        ApiResponse<List<RolDTO>> response = rolService.listarRoles();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{idrol}/permisos")
    public ResponseEntity<ApiResponse<Set<Integer>>> getPermisosByRol(@PathVariable("idrol") Integer rolId) {
        ApiResponse<Set<Integer>> response = rolService.permisosByRol(rolId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RolDTO>> add(@RequestBody RolDTO input) {
        ApiResponse<RolDTO> response = rolService.save(input);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<RolDTO>> updateRol(@RequestBody RolDTO input) {
        ApiResponse<RolDTO> response = rolService.update(input);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{idrol}")
    public ResponseEntity<ApiResponse<Rol>> delete(@PathVariable("idrol") int id) {
        return ResponseEntity.ok(this.rolService.delete(id));

    }

}
