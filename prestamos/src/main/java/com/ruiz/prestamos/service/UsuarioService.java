package com.ruiz.prestamos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ruiz.prestamos.persistence.dto.UsuarioDTO;
import com.ruiz.prestamos.persistence.entity.Usuario;
import com.ruiz.prestamos.persistence.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private ModelMapper modelMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    public Usuario get(int id){
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario save(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    public boolean delete(int id){
        try {
            usuarioRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean exist(Integer id){
        boolean result = false;
        if(id != null && usuarioRepository.existsById(id)){
            result = true;
        }
        return result;
    }

    public UsuarioDTO convertirADTO(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    public Usuario convertirAEntidad(UsuarioDTO usuarioDTO) {
        return modelMapper.map(usuarioDTO, Usuario.class);
    }

    public List<UsuarioDTO> convertirListaADTO(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> obtenerTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return convertirListaADTO(usuarios);
    }


}
