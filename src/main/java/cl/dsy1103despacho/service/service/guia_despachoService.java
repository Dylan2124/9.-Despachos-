package cl.dsy1103despacho.service.service;

import cl.dsy1103despacho.service.dto.guia_despachoRequestDTO;
import cl.dsy1103despacho.service.dto.guia_despachoResponseDTO;
import cl.dsy1103despacho.service.model.guia_despacho;
import cl.dsy1103despacho.service.repository.guia_despachoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class guia_despachoService {

    private final guia_despachoRepository repository;

    // ── MAPEO PRIVADO: Entidad → ResponseDTO ─────────
    private guia_despachoResponseDTO mapToDTO(guia_despacho guia) {
        return new guia_despachoResponseDTO(
                guia.getIdGuia(),
                guia.getIdPedido(),
                guia.getDireccionEnvio(),
                guia.getEstadoEnvio(),
                guia.getFechaEstimada()
        );
    }

    // ── OBTENER TODOS ────────────────────────────────
    public List<guia_despachoResponseDTO> obtenerTodos() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ── OBTENER POR ID ───────────────────────────────
    public Optional<guia_despachoResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::mapToDTO);
    }

    // ── GUARDAR (CREAR) ──────────────────────────────
    public guia_despachoResponseDTO guardar(guia_despachoRequestDTO dto) {

        // RuntimeException si ya existe un despacho para ese pedido
        if (repository.findByIdPedido(dto.getIdPedido()).isPresent()) {
            throw new RuntimeException("Ya existe una guía de despacho para el pedido ID: " + dto.getIdPedido());
        }

        guia_despacho guia = new guia_despacho();
        guia.setIdPedido(dto.getIdPedido());
        guia.setDireccionEnvio(dto.getDireccionEnvio());
        guia.setEstadoEnvio("PREPARANDO");
        guia.setFechaEstimada(LocalDateTime.now().plusDays(3));

        return mapToDTO(repository.save(guia));
    }

    // ── ACTUALIZAR ───────────────────────────────────
    // Solo se permite actualizar la dirección de envio
    public Optional<guia_despachoResponseDTO> actualizar(Long id, guia_despachoRequestDTO dto) {
        return repository.findById(id).map(existente -> {

            // Validamos que no intente cambiar el pedido a uno que ya tiene despacho
            if (!existente.getIdPedido().equals(dto.getIdPedido()) &&
                    repository.findByIdPedido(dto.getIdPedido()).isPresent()) {
                throw new RuntimeException("El nuevo pedido ID ya tiene un despacho asociado");
            }

            existente.setIdPedido(dto.getIdPedido());
            existente.setDireccionEnvio(dto.getDireccionEnvio());
            // No actualizamos estado y fecha aquí por seguridad

            return mapToDTO(repository.save(existente));
        });
    }

    // ── ELIMINAR ─────────────────────────────────────
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    // ── BÚSQUEDAS PERSONALIZADAS ─────────────────────
    public Optional<guia_despachoResponseDTO> buscarPorIdPedido(Long idPedido) {
        return repository.findByIdPedido(idPedido).map(this::mapToDTO);
    }
}