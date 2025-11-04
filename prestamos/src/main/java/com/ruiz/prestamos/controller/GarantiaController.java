package com.ruiz.prestamos.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruiz.prestamos.persistence.dto.DocumentoDTO;
import com.ruiz.prestamos.persistence.dto.GarantiaDTO;
import com.ruiz.prestamos.persistence.entity.Documento;
import com.ruiz.prestamos.service.DocumentoService;
import com.ruiz.prestamos.service.GarantiaService;
import com.ruiz.prestamos.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/garantia")
@RequiredArgsConstructor
public class GarantiaController {
    private final GarantiaService garantiaService;
    private final DocumentoService documentoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GarantiaDTO>>> getAll() {
        return ResponseEntity.ok(this.garantiaService.getAll());
    }

    @GetMapping("/{idGarantia}")
    public ResponseEntity<ApiResponse<GarantiaDTO>> get(@PathVariable int idGarantia) {
        return ResponseEntity.ok(this.garantiaService.get(idGarantia));
    }

    @GetMapping("/documentos/{idGarantia}")
    public ResponseEntity<Resource> descargarPorGarantia(@PathVariable("idGarantia") Integer idGarantia) throws IOException {
        List<Documento> documentos = documentoService.getByGarantia(idGarantia);
        return buildResponse(this.garantiaService.generarZip(documentos), "garantia_" + idGarantia + ".zip");
    }

    @GetMapping("/pago/{idPago}")
    public ResponseEntity<Resource> descargarPorPago(@PathVariable("idPago") Integer idPago) throws IOException {
        List<Documento> documentos = garantiaService.getDocumentosByPago(idPago);
        return buildResponse(this.garantiaService.generarZip(documentos), "pago_" + idPago + ".zip");
    }

    

    private ResponseEntity<Resource> buildResponse(ByteArrayResource resource, String nombreArchivo)
            throws IOException {
        if (resource == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GarantiaDTO>> add(
            @RequestPart("garantiaDTO") GarantiaDTO garantiaDTO,
            @RequestPart("documentos") MultipartFile[] documentos) {
        return ResponseEntity.ok(this.garantiaService.save(garantiaDTO, documentos));
    }

    /*
     * @PutMapping
     * public ResponseEntity<ApiResponse<GarantiaDTO>> update(@RequestBody
     * GarantiaDTO inGarantia) {
     * return ResponseEntity.ok(this.garantiaService.save(inGarantia));
     * }
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable("id") int idGarantia) {
        return ResponseEntity.ok(this.garantiaService.delete(idGarantia));
    }

}
