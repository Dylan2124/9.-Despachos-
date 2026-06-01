package cl.dsy1103despacho.service.service;

import cl.dsy1103despacho.service.dto.NotificacionRequestDTO;
import cl.dsy1103despacho.service.dto.PedidoExternoDTO;
import cl.dsy1103despacho.service.dto.guia_despachoRequestDTO;
import cl.dsy1103despacho.service.dto.guia_despachoResponseDTO;
import cl.dsy1103despacho.service.model.guia_despacho;
import cl.dsy1103despacho.service.repository.guia_despachoRepository;


import cl.dsy1103despacho.service.client.PedidoClient;
import cl.dsy1103despacho.service.client.NotificacionClient;
import lombok.extern.slf4j.Slf4j; // Necesario para los logs en consola


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class guia_despachoService {

    private final guia_despachoRepository repository;

    private final PedidoClient pedidoClient;
    private final NotificacionClient notificacionClient;

    // ── MAPEO  Entidad → ResponseDTO ─────────
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

    // ── GUARDAR (CREAR) ── CON OPENFEIGN ────────────────────
    public guia_despachoResponseDTO guardar(guia_despachoRequestDTO dto) {

        // RuntimeException si ya existe un despacho para ese pedido (Tu validación de siempre)
        if (repository.findByIdPedido(dto.getIdPedido()).isPresent()) {
            throw new RuntimeException("Ya existe una guía de despacho para el pedido ID: " + dto.getIdPedido());
        }

        PedidoExternoDTO datosPedido = null;


        // ACCIÓN FEIGN A: Llamamos a ms-pedidos
        try {
            log.info("Llamando por Feign a ms-pedidos para rescatar información del pedido: {}", dto.getIdPedido());
            datosPedido = pedidoClient.obtenerPedidoPorId(dto.getIdPedido());
        } catch (Exception e) {
            log.warn("TOLERANCIA A FALLOS ACTIVA: No se pudo conectar con ms-pedidos: {}", e.getMessage());
        }

        guia_despacho guia = new guia_despacho();
        guia.setIdPedido(dto.getIdPedido());

        if (datosPedido != null) {
            log.info("Dirección obtenida desde ms-pedidos: {}", datosPedido.getDireccionEnvio());
            guia.setDireccionEnvio(datosPedido.getDireccionEnvio());
        } else {
            log.warn("Usando dirección alternativa enviada por el RequestDTO local.");
            guia.setDireccionEnvio(dto.getDireccionEnvio());
        }

        guia.setEstadoEnvio("PREPARANDO");
        guia.setFechaEstimada(LocalDateTime.now().plusDays(3));

        guia_despacho guiaGuardada = repository.save(guia);

        // ACCIÓN FEIGN B: Llamamos a ms-notificaciones si logramos tener los datos completos
        if (datosPedido != null) {
            try {
                log.info("Preparando datos para ms-notificaciones...");

                NotificacionRequestDTO aviso = new NotificacionRequestDTO(
                        datosPedido.getIdUsuario(),
                        dto.getIdPedido(),
                        "EMAIL",
                        "¡Tu despacho ha sido programado! El paquete irá a: " + datosPedido.getDireccionEnvio()
                );

                notificacionClient.enviarNotificacion(aviso);
                log.info("¡Notificación enviada con éxito!");
            } catch (Exception e) {
                log.warn("TOLERANCIA A FALLOS ACTIVA: El despacho se creó pero no se envió el correo. ms-notificaciones caído: {}", e.getMessage());
            }
        }

        return mapToDTO(guiaGuardada);
    }

    // ── ACTUALIZAR ───────────────────────────────────
    public Optional<guia_despachoResponseDTO> actualizar(Long id, guia_despachoRequestDTO dto) {
        return repository.findById(id).map(existente -> {

            if (!existente.getIdPedido().equals(dto.getIdPedido()) &&
                    repository.findByIdPedido(dto.getIdPedido()).isPresent()) {
                throw new RuntimeException("El nuevo pedido ID ya tiene un despacho asociado");
            }

            existente.setIdPedido(dto.getIdPedido());
            existente.setDireccionEnvio(dto.getDireccionEnvio());

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