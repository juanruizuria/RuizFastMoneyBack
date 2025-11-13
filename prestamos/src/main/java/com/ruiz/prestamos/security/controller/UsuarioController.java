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

import com.ruiz.prestamos.security.persistence.dto.UsuarioDTO;
import com.ruiz.prestamos.security.persistence.entity.Usuario;
import com.ruiz.prestamos.security.service.UserSecurityService;
import com.ruiz.prestamos.util.ApiResponse;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
    private final UserSecurityService service;

    public UsuarioController(UserSecurityService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> listarRoles() {
        ApiResponse<List<UsuarioDTO>> response = service.listarUsuarios();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Integer id) {
        Usuario user = service.getById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioDTO>> add(@RequestBody UsuarioDTO input) {
        ApiResponse<UsuarioDTO> response = service.createUser(input);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping()
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizarUsuario(@RequestBody UsuarioDTO dto) {
        ApiResponse<UsuarioDTO> actualizado = service.actualizarUsuario(dto);
        return ResponseEntity.status(actualizado.getStatus()).body(actualizado);
    }

    @PutMapping("/deshabilitar/{idUsuario}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> deshabilitarUsuario(@PathVariable("idUsuario") int id) {
        return ResponseEntity.ok(this.service.deshabilitarUsuario(id));
    }

    @DeleteMapping("/{idrol}")
    public ResponseEntity<ApiResponse<Usuario>> delete(@PathVariable("idrol") int id) {
        return ResponseEntity.ok(this.service.eliminarUsuario(id));

    }

}
