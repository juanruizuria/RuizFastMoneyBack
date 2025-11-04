package com.ruiz.prestamos.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruiz.prestamos.persistence.dto.DocumentoDTO;
import com.ruiz.prestamos.persistence.entity.Garantia;
import com.ruiz.prestamos.service.DocumentoService;
import com.ruiz.prestamos.util.ApiResponse;

@RestController
@RequestMapping("/api/documento")
public class DocumentoController {
    private final DocumentoService service;
  

    public DocumentoController(DocumentoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentoDTO>>> getAll() {
        return ResponseEntity.ok(this.service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentoDTO>> get(@PathVariable int id) {
        return ResponseEntity.ok(this.service.get(id));
    }

    

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentoDTO>> add(
            @RequestParam("garantia") Garantia garantia,
            @RequestPart("archivo") MultipartFile archivo) {
            
        return ResponseEntity.ok(this.service.save(garantia, archivo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable("id") int idDocumento) {
        return ResponseEntity.ok(this.service.delete(idDocumento));
    }

}
