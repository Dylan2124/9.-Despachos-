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
            return ResponseEntity.ok("No se encontraron despachos");
        }
        return new ResponseEntity<>(service.obtenerTodos(), HttpStatus.OK);
    }

    // GET ──────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        java.util.Optional<guia_despachoResponseDTO> guiaOptional = service.obtenerPorId(id);

        if (guiaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró un despacho con el ID: " + id);
        }

        return ResponseEntity.ok(guiaOptional.get());
    }

    // POST ────────────────────
    @PostMapping
    public ResponseEntity<guia_despachoResponseDTO> guardar(
            @Valid @RequestBody guia_despachoRequestDTO request) {
        guia_despachoResponseDTO response = service.guardar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Código 201
    }

    // PUT ──────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<guia_despachoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody guia_despachoRequestDTO request) {
        return service.actualizar(id, request)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE ─────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Código 204
    }

    // BUSCAR POR ID DE PEDIDO
    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<?> buscarPorIdPedido(@PathVariable Long idPedido) {
        java.util.Optional<guia_despachoResponseDTO> guiaOptional = service.buscarPorIdPedido(idPedido);

        if (guiaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ninguna guía asociada al pedido con ID: " + idPedido);
        }

        return ResponseEntity.ok(guiaOptional.get());
    }
}
