package com.ruiz.prestamos.service.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestException extends RuntimeException{
    private ErrorDTO errorDTO;
    
    public RequestException(ErrorDTO errorDTO, String message){
        super(message);
        this.errorDTO = errorDTO;
    }

}
