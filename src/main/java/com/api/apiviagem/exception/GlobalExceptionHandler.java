package com.api.apiviagem.exception;

import com.api.apiviagem.DTO.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlerResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now(),request.getRequestURI()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponseDTO> handlerServiceUnavailableException(ServiceUnavailableException ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now(),request.getRequestURI()), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handlerException(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now(),request.getRequestURI()), HttpStatus.NOT_FOUND);
    }

}
