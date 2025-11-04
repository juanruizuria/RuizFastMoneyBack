package com.ruiz.prestamos.persistence.dto;

import java.util.List;

public record MenuDTO(
    String label,
    String icon,
    String route,
    List<MenuDTO> children
) {
    public MenuDTO(String label, String icon, String route) {
        this(label, icon, route, List.of());
    }
}

