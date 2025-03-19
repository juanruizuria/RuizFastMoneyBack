package com.ruiz.prestamos.controller;

import java.util.List;

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

import com.ruiz.prestamos.persistence.dto.PagoDTO;
import com.ruiz.prestamos.persistence.entity.Pago;
import com.ruiz.prestamos.service.PagoService;

@RestController
@RequestMapping("/api/pago")
public class PagoController {
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public ResponseEntity<List<PagoDTO>> getAll() {
        List<Pago> pagos = this.pagoService.getAll();
        if (pagos == null || pagos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(this.pagoService.convertirListaADTO(pagos));
    }

    @GetMapping("/{idPago}")
    public ResponseEntity<PagoDTO> get(@PathVariable int idPago) {
        Pago pago = this.pagoService.get(idPago);
        if (pago == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(this.pagoService.convertirADTO(pago));
    }

    @PostMapping
    public ResponseEntity<PagoDTO> add(@RequestBody Pago inputPago) {
        if (this.pagoService.exist(inputPago.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Pago Pago = this.pagoService.save(inputPago);
        return ResponseEntity.ok(this.pagoService.convertirADTO(Pago));
    }

    @PutMapping
    public ResponseEntity<PagoDTO> update(@RequestBody Pago inputPago) {
        if (!this.pagoService.exist(inputPago.getId())) {
            return ResponseEntity.notFound().build();
        }
        Pago Pago = this.pagoService.save(inputPago);
        return ResponseEntity.ok(this.pagoService.convertirADTO(Pago));
    }

    @DeleteMapping("/{idPago}")
    public ResponseEntity<String> delete(@PathVariable int idPago) {
        return (this.pagoService.delete(idPago))
                ? ResponseEntity.ok("Recurso eliminado correctamente")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recurso con ID " + idPago + " no encontrado");
    }


}
