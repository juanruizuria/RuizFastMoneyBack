package com.ruiz.prestamos.exception;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDTO {
    private String message;
    private String code;
    private String detail;
    private HttpStatus status;
}
