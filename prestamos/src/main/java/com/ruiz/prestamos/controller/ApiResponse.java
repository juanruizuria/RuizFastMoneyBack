package com.ruiz.prestamos.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private int status;  // Código HTTP
    private String type; // SUCCESS, WARNING, ERROR
    private String message; // Mensaje para el usuario
    private T data; // Datos opcionales

    public ApiResponse(int status, String type, String message, T data) {
        this.status = status;
        this.type = type;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, "SUCCESS", message, data);
    }

    public static <T> ApiResponse<T> warning(String message, T data) {
        return new ApiResponse<>(400, "WARNING", message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, "ERROR", message, null);
    }

}
