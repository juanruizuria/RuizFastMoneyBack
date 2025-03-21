package com.ruiz.prestamos.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruiz.prestamos.persistence.dto.PrestamoDTO;
import com.ruiz.prestamos.persistence.entity.Prestamo;
import com.ruiz.prestamos.service.PrestamoService;

@RestController
@RequestMapping("/api/prestamo")
public class PrestamoController {
    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PrestamoDTO>>> getAll() {
        try {
            List<Prestamo> prestamos = this.prestamoService.getAll();
            return ResponseEntity.ok(
                    ApiResponse.success("usuarios encontrados", this.prestamoService.convertirListaADTO(prestamos)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.warning("No existen prestamos", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor: " + e.getMessage()));
        }

    }

    @GetMapping("/{idPrestamo}")
    public ResponseEntity<ApiResponse<PrestamoDTO>> get(@PathVariable int idPrestamo) {
        try {
            Prestamo prestamo = this.prestamoService.get(idPrestamo);
            return ResponseEntity
                    .ok(ApiResponse.success("Usuario encontrado", this.prestamoService.convertirADTO(prestamo)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.warning(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<PrestamoDTO> add(@RequestBody Prestamo inputPrestamo) {
        if (this.prestamoService.exist(inputPrestamo.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Prestamo prestamo = this.prestamoService.save(inputPrestamo);
        return ResponseEntity.ok(this.prestamoService.convertirADTO(prestamo));
    }

    @PutMapping
    public ResponseEntity<PrestamoDTO> update(@RequestBody Prestamo inputPrestamo) {
        if (!this.prestamoService.exist(inputPrestamo.getId())) {
            return ResponseEntity.notFound().build();
        }
        Prestamo prestamo = this.prestamoService.save(inputPrestamo);
        return ResponseEntity.ok(this.prestamoService.convertirADTO(prestamo));
    }

    @DeleteMapping("/{idPrestamo}")
    public ResponseEntity<String> delete(@PathVariable int idPrestamo) {
        return (this.prestamoService.delete(idPrestamo))
                ? ResponseEntity.ok("Recurso eliminado correctamente")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recurso con ID " + idPrestamo + " no encontrado");
    }

}
