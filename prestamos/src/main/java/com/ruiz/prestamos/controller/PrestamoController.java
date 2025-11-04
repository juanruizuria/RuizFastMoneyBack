package com.ruiz.prestamos.controller;

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
import com.ruiz.prestamos.persistence.dto.PrestamoDTO;
import com.ruiz.prestamos.service.PrestamoService;
import com.ruiz.prestamos.util.ApiResponse;

@RestController
@RequestMapping("/api/prestamo")
public class PrestamoController {
    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PrestamoDTO>>> getAll() {
        return ResponseEntity.ok(this.prestamoService.getAll());
    }

   
    @GetMapping("/{idPrestamo}")
    public ResponseEntity<ApiResponse<PrestamoDTO>> get(@PathVariable int idPrestamo) {
        return ResponseEntity.ok(this.prestamoService.get(idPrestamo));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PrestamoDTO>> add(@RequestBody PrestamoDTO inPrestamo) {
        return ResponseEntity.ok(this.prestamoService.save(inPrestamo));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PrestamoDTO>> update(@RequestBody PrestamoDTO inPrestamo) {
        return ResponseEntity.ok(this.prestamoService.update(inPrestamo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable("id") int idPrestamo) {
        return ResponseEntity.ok(this.prestamoService.delete(idPrestamo));
    }

}
