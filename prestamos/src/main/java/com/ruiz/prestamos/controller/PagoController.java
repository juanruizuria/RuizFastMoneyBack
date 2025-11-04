package com.ruiz.prestamos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruiz.prestamos.persistence.dto.PagoDTO;
import com.ruiz.prestamos.service.PagoService;
import com.ruiz.prestamos.util.ApiResponse;

@RestController
@RequestMapping("/api/pago")
public class PagoController {
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PagoDTO>>> getAll() {
        return ResponseEntity.ok(this.pagoService.getAll());
    }

    @GetMapping("/prestamo/{idPrestamo}")
    public ResponseEntity<ApiResponse<List<PagoDTO>>> getByPrestamo(@PathVariable int idPrestamo) {
        return ResponseEntity.ok(this.pagoService.getByPrestamo(idPrestamo));
    }

    @GetMapping("/{idPago}")
    public ResponseEntity<ApiResponse<PagoDTO>> get(@PathVariable int idPago) {
        return ResponseEntity.ok(this.pagoService.get(idPago));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PagoDTO>> pagar(@RequestBody PagoDTO inputPago) {
        return ResponseEntity.ok(this.pagoService.pagar(inputPago));
    }
        

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoDTO>> delete(@PathVariable("id") int idPago) {
        return ResponseEntity.ok(this.pagoService.delete(idPago));
    }



}
