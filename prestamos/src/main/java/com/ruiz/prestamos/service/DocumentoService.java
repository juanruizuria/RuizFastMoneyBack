package com.ruiz.prestamos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruiz.prestamos.config.GenericMapper;
import com.ruiz.prestamos.persistence.dto.DocumentoDTO;
import com.ruiz.prestamos.persistence.entity.Garantia;
import com.ruiz.prestamos.persistence.entity.Documento;
import com.ruiz.prestamos.persistence.repository.GarantiaRepository;
import com.ruiz.prestamos.persistence.repository.DocumentoRepository;
import com.ruiz.prestamos.util.ApiResponse;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DocumentoService {
    private final DocumentoRepository repository;
    private GarantiaRepository gatantiaRepository;
    private GenericMapper mapper;

    @Value("${app.upload.dir:uploads/Documentos}") // configurable en application.properties
    private String uploadDir;

    public DocumentoService(DocumentoRepository repository,
            GarantiaRepository gatantiaRepository,
            GenericMapper mapper) {
        this.repository = repository;
        this.gatantiaRepository = gatantiaRepository;
        this.mapper = mapper;
    }

    public ApiResponse<List<DocumentoDTO>> getAll() {
        try {
            List<Documento> documentos = repository.findAll();
            if (documentos.isEmpty()) {
                throw new NoSuchElementException("No existen documentos");
            }
            return ApiResponse.success("documentos encontradas",
                    mapper.convertirListaADTO(documentos, DocumentoDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public List<Documento> getByGarantia(Integer id) {
        List<Documento> documentos = new ArrayList<>();
        try {
            documentos = repository.findByGarantiaId(id);

        } catch (Exception e) {
            System.out.println("Error al obtener documentos: " + e.getMessage());
        }
        return documentos;
    }

    public ApiResponse<DocumentoDTO> get(int id) {
        try {
            Documento Documento = repository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe Documento con id: " + id));
            return ApiResponse.success("Documento encontrada", mapper.convertirADTO(Documento, DocumentoDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<DocumentoDTO> save(Garantia garantia, MultipartFile archivo) {
        try {

            // crear carpeta si no existe
            Path carpeta = Paths.get(uploadDir);
            if (!Files.exists(carpeta)) {
                Files.createDirectories(carpeta);
            }
            System.out.println("📂 Carpeta de documentos: " + carpeta);

            // generar nombre único
            String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path rutaArchivo = carpeta.resolve(nombreArchivo);

            // guardar en disco
            Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // guardar en BD
            Documento doc = new Documento();
            doc.setNombre(archivo.getOriginalFilename());
            doc.setTipo(archivo.getContentType());
            doc.setSize(archivo.getSize());
            doc.setRuta(rutaArchivo.toString());
            doc.setGarantia(garantia);
            repository.save(doc);
            return ApiResponse.success("Documento guardada correctamente",
                    mapper.convertirADTO(doc, DocumentoDTO.class));

        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el documento en el servidor", e);
        }
    }

    public ApiResponse<Boolean> delete(Integer id) {
        try {
            Documento doc = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Documento no encontrada"));

            if (doc.getRuta() != null) {
                Path path = Paths.get(doc.getRuta());
                Files.deleteIfExists(path);
            }
            repository.delete(doc);
            return ApiResponse.success("Documento eliminada", true);
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), false);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

}
