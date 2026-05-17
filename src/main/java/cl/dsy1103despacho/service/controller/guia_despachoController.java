package cl.dsy1103despacho.service.controller;

import cl.dsy1103despacho.service.dto.guia_despachoRequestDTO;
import cl.dsy1103despacho.service.dto.guia_despachoResponseDTO;
import cl.dsy1103despacho.service.service.guia_despachoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
public class guia_despachoController {

    private final guia_despachoService service;

    // GET ───────────────────────
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        List<guia_despachoResponseDTO> lista = service.obtenerTodos();
        if (lista.isEmpty()){
            return ResponseEntity.ok(Map.of("mensaje", "No se encontraron despachos"));
        }
        return ResponseEntity.ok(lista);
    }

    // GET POR ID ──────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<guia_despachoResponseDTO> guiaOptional = service.obtenerPorId(id);

        if (guiaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontró un despacho con el ID: " + id));
        }

        return ResponseEntity.ok(guiaOptional.get());
    }

    // POST ────────────────────
    @PostMapping
    public ResponseEntity<guia_despachoResponseDTO> guardar(
            @Valid @RequestBody guia_despachoRequestDTO request) {
        guia_despachoResponseDTO response = service.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT ──────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody guia_despachoRequestDTO request) {

        Optional<guia_despachoResponseDTO> actualizado = service.actualizar(id, request);

        if (actualizado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se puede actualizar. No existe el despacho con ID: " + id));
        }

        return ResponseEntity.ok(actualizado.get());
    }

    // DELETE ─────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (service.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se puede eliminar. No existe el despacho con ID: " + id));
        }
        service.eliminar(id);

        return ResponseEntity.ok(Map.of("mensaje", "El despacho con ID " + id + " se eliminó con éxito."));
    }

    // BUSCAR POR ID DE PEDIDO ─────────────────────────
    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<?> buscarPorIdPedido(@PathVariable Long idPedido) {
        Optional<guia_despachoResponseDTO> guiaOptional = service.buscarPorIdPedido(idPedido);

        if (guiaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontró ninguna guía asociada al pedido con ID: " + idPedido));
        }

        return ResponseEntity.ok(guiaOptional.get());
    }
}