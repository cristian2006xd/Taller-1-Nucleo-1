package com.ruedalibre.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String manejarRecursoNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        model.addAttribute("titulo", "Recurso no encontrado");
        model.addAttribute("mensaje", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(ReglaNegocioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String manejarReglaNegocio(ReglaNegocioException ex, Model model) {
        log.warn("Regla de negocio violada: {}", ex.getMessage());
        model.addAttribute("titulo", "Operación no permitida");
        model.addAttribute("mensaje", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String manejarErrorGeneral(Exception ex, Model model) {
        log.error("Error inesperado: {}", ex.getMessage());
        model.addAttribute("titulo", "Error interno");
        model.addAttribute("mensaje", "Ocurrió un error inesperado. Por favor intente de nuevo.");
        return "error";
    }
}
