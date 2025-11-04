package com.ruiz.prestamos.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruiz.prestamos.persistence.dto.PersonaDTO;
import com.ruiz.prestamos.persistence.entity.Persona;
import com.ruiz.prestamos.service.PersonaService;
import com.ruiz.prestamos.util.ApiResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/persona")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping("/find")
    public ResponseEntity<ApiResponse<List<PersonaDTO>>> find(
            @RequestParam(name = "tipo", required = false) String tipo,
            @RequestParam(name = "activo", required = false) Boolean activo,
            @RequestParam(name = "inicio", required = false) Date inicio,
            @RequestParam(name = "fin", required = false) Date fin) {
        return ResponseEntity.ok(
                personaService.listarPersonas(tipo, activo, inicio, fin));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PersonaDTO>>> getAll() {
        return ResponseEntity.ok(this.personaService.getAll());
    }

    @GetMapping("/{idpersona}")
    public ResponseEntity<ApiResponse<PersonaDTO>> get(@PathVariable("idpersona") int id) {
        return ResponseEntity.ok(this.personaService.get(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PersonaDTO>> add(@RequestBody Persona inputpersona) {
        inputpersona.setId(null);
        return ResponseEntity.ok(this.personaService.save(inputpersona));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PersonaDTO>> update(@RequestBody Persona inputpersona) {
        return ResponseEntity.ok(this.personaService.update(inputpersona));
    }

    @DeleteMapping("/{idpersona}")
    public ResponseEntity<ApiResponse<PersonaDTO>> delete(@PathVariable("idpersona") int id) {
        return ResponseEntity.ok(this.personaService.delete(id));

    }

}
