package com.ruiz.prestamos.persistence.dto;
import java.util.List;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
    private List<String> roles;
    private List<String> permisos;
}
