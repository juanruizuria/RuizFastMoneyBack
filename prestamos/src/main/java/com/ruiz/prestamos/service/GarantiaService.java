package com.ruiz.prestamos.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ruiz.prestamos.config.GenericMapper;
import com.ruiz.prestamos.persistence.dto.DocumentoDTO;
import com.ruiz.prestamos.persistence.dto.GarantiaDTO;
import com.ruiz.prestamos.persistence.entity.Documento;
import com.ruiz.prestamos.persistence.entity.Garantia;
import com.ruiz.prestamos.persistence.entity.Pago;
import com.ruiz.prestamos.persistence.entity.Prestamo;
import com.ruiz.prestamos.persistence.repository.GarantiaRepository;
import com.ruiz.prestamos.persistence.repository.PagoRepository;
import com.ruiz.prestamos.persistence.repository.PrestamoRepository;
import com.ruiz.prestamos.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GarantiaService {
    private final GarantiaRepository garantiaRepository;
    private final PrestamoRepository prestamoRepository;
    private final PagoRepository pagoRepository;
    private final DocumentoService documentoService;
    private final GenericMapper mapper;

    public ApiResponse<List<GarantiaDTO>> getAll() {
        try {
            List<Garantia> garantias = garantiaRepository.findAll();
            if (garantias.isEmpty()) {
                ApiResponse.warning("No existen garantias para el prestamo", null);
            }
            return ApiResponse.success("Garantias encontrados",
                    mapper.convertirListaADTO(garantias, GarantiaDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<GarantiaDTO> get(int id) {
        try {
            Garantia Garantia = garantiaRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe garantia con id: " + id));
            return ApiResponse.success("Garantia encontrada", mapper.convertirADTO(Garantia, GarantiaDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<List<GarantiaDTO>> getByPrestamo(int prestamoId) {
        try {
            List<Garantia> garantias = garantiaRepository.findByPrestamoId(prestamoId);
            if (garantias.isEmpty()) {
                ApiResponse.warning("No existen garantias para el prestamo", prestamoId);
            }
            return ApiResponse.success("Garantias encontrados",
                    mapper.convertirListaADTO(garantias, GarantiaDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public ApiResponse<List<GarantiaDTO>> getByPago(int pagoId) {
        try {
            List<Garantia> garantias = garantiaRepository.findByPagoId(pagoId);
            if (garantias.isEmpty()) {
                ApiResponse.warning("No existen garantias para el pago", pagoId);
            }
            return ApiResponse.success("Garantias encontrados",
                    mapper.convertirListaADTO(garantias, GarantiaDTO.class));
        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public List<Documento> getDocumentosByPago(Integer pagoId) {
        List<Garantia> garantias = garantiaRepository.findByPagoId(pagoId);

        return garantias.stream()
                .flatMap(g -> g.getDocumentos().stream())
                .toList();
    }

    @Transactional()
    public ApiResponse<GarantiaDTO> save(GarantiaDTO garantiaDTO, MultipartFile[] documentos) {
        try {
            Garantia garantia = mapper.convertirAEntidad(garantiaDTO, Garantia.class);

            Prestamo prestamo = (garantiaDTO.getIdPrestamo() == null) ? null
                    : prestamoRepository.findById(garantiaDTO.getIdPrestamo())
                            .orElseThrow(() -> new NoSuchElementException(
                                    "No existe préstamo con id: " + garantiaDTO.getIdPrestamo()));
            Pago pago = (garantiaDTO.getIdPago() == null) ? null
                    : pagoRepository.findById(garantiaDTO.getIdPago()).orElseThrow(
                            () -> new NoSuchElementException("No existe préstamo con id: " + garantiaDTO.getIdPago()));

            if (prestamo == null && pago == null) {
                return ApiResponse.error("Se necesita un prestamo/pago para la garantia");
            }

            // garantia.setId(null);
            garantia.setFechaRegistro(LocalDate.now());
            garantia.setPrestamo(prestamo);
            garantia.setPago(pago);
            Garantia newGarantia = garantiaRepository.save(garantia);

            // si hay documentos, guardarlos
            List<DocumentoDTO> listaDocs = new ArrayList<>();
            if (documentos != null) {
                for (MultipartFile archivo : documentos) {
                    if (!archivo.isEmpty()) {
                        ApiResponse<DocumentoDTO> respDoc = documentoService.save(newGarantia, archivo);
                        if (respDoc.getStatus() == 200) {
                            listaDocs.add(respDoc.getData());
                        }
                    }
                }
            }
            GarantiaDTO respuesta = mapper.convertirADTO(newGarantia, GarantiaDTO.class);
            respuesta.setDocumentos(listaDocs);

            return ApiResponse.success("Garantía guardada", respuesta);
        } catch (NoSuchElementException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al crear la garantia: " + e.getMessage());
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<Boolean> delete(int id) {
        try {
            Garantia garantia = garantiaRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No existe garantia con id: " + id));

            garantiaRepository.delete(garantia);
            return ApiResponse.success("Garantia eliminada", true);

        } catch (NoSuchElementException e) {
            return ApiResponse.warning(e.getMessage(), false);
        } catch (Exception e) {
            return ApiResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }

    public boolean exist(Integer id) {
        boolean result = false;
        if (id != null && garantiaRepository.existsById(id)) {
            result = true;
        }
        return result;
    }


    public ByteArrayResource generarZip(List<Documento> documentos) throws IOException {
        if (documentos.isEmpty()) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Documento doc : documentos) {
                Path path = Paths.get(doc.getRuta());
                if (Files.exists(path)) {
                    zos.putNextEntry(new ZipEntry(doc.getNombre()));
                    Files.copy(path, zos);
                    zos.closeEntry();
                }
            }
        }
        return new ByteArrayResource(baos.toByteArray());
    }

}