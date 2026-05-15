package cl.dsy1103despacho.service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── ERROR DE VALIDACIÓN (@Valid) ─────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationErrors(
            MethodArgumentNotValidException ex){

        // LinkedHashMap mantiene el orden de inserción.
        Map<String, String> errores = new LinkedHashMap<>();

        // getFieldErrors() devuelve uno por cada campo inválido.
        // getField()  → nombre del campo del DTO
        // getDefaultMessage() → el texto del message= en la anotación
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));

        // 400 Bad Request: el cliente envió datos inválidos.
        return ResponseEntity.badRequest().body(errores);
    }

    // ── ERROR DE NEGOCIO (despacho duplicado, etc.) ──
    // Se dispara cuando el Service lanza RuntimeException,
    // por ejemplo: "Ya existe una guía de despacho para el pedido ID: 99"
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> handleRuntimeException(
            RuntimeException ex){
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", ex.getMessage());

        // 400 Bad Request: el cliente envió un dato que choca con las reglas.
        // Usamos 400 y no 500 porque el servidor funcionó correctamente;
        // fue la acción del cliente la que causó el problema.
        return ResponseEntity.badRequest().body(error);
    }
}