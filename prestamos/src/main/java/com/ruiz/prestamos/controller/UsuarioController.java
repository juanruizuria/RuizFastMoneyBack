package com.ruiz.prestamos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruiz.prestamos.persistence.dto.UsuarioDTO;
import com.ruiz.prestamos.persistence.entity.Usuario;
import com.ruiz.prestamos.service.UsuarioService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAll() {
        List<Usuario> usuarios = this.usuarioService.getAll();
        if (usuarios == null || usuarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(this.usuarioService.convertirListaADTO(usuarios));
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioDTO> get(@PathVariable int idUsuario) {
        Usuario usuario = this.usuarioService.get(idUsuario);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(this.usuarioService.convertirADTO(usuario));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> add(@RequestBody Usuario inputUsuario) {
        if (this.usuarioService.exist(inputUsuario.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Usuario usuario = this.usuarioService.save(inputUsuario);
        return ResponseEntity.ok(this.usuarioService.convertirADTO(usuario));
    }

    @PutMapping
    public ResponseEntity<UsuarioDTO> update(@RequestBody Usuario inputUsuario) {
        if (!this.usuarioService.exist(inputUsuario.getId())) {
            return ResponseEntity.notFound().build();
        }
        Usuario usuario = this.usuarioService.save(inputUsuario);
        return ResponseEntity.ok(this.usuarioService.convertirADTO(usuario));
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<String> delete(@PathVariable int idUsuario) {
        return (this.usuarioService.delete(idUsuario))
                ? ResponseEntity.ok("Recurso eliminado correctamente")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recurso con ID " + idUsuario + " no encontrado");
    }

}
