package cl.dsy1103despacho.service.service;

import cl.dsy1103despacho.service.client.NotificacionClient;
import cl.dsy1103despacho.service.client.PedidoClient;
import cl.dsy1103despacho.service.dto.NotificacionRequestDTO;
import cl.dsy1103despacho.service.dto.PedidoExternoDTO;
import cl.dsy1103despacho.service.dto.guia_despachoRequestDTO;
import cl.dsy1103despacho.service.dto.guia_despachoResponseDTO;
import cl.dsy1103despacho.service.model.guia_despacho;
import cl.dsy1103despacho.service.repository.guia_despachoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class guia_despachoServiceTest {

    @Mock
    private guia_despachoRepository repository;

    @Mock
    private PedidoClient pedidoClient;

    @Mock
    private NotificacionClient notificacionClient;

    @InjectMocks
    private guia_despachoService service;

    private guia_despacho guiaFalsa;
    private guia_despachoRequestDTO requestDTO;
    private PedidoExternoDTO pedidoFalso;

    @BeforeEach
    void setUp() {
        guiaFalsa = new guia_despacho();
        guiaFalsa.setIdGuia(1L);
        guiaFalsa.setIdPedido(100L);
        guiaFalsa.setDireccionEnvio("Calle Falsa 123");
        guiaFalsa.setEstadoEnvio("PREPARANDO");
        guiaFalsa.setFechaEstimada(LocalDateTime.now().plusDays(3));

        requestDTO = new guia_despachoRequestDTO();
        requestDTO.setIdPedido(100L);
        requestDTO.setDireccionEnvio("Calle Falsa 123");

        pedidoFalso = new PedidoExternoDTO();
        // Asumiendo que tu DTO tiene estos métodos, si no, ajústalos.
        pedidoFalso.setIdUsuario(99L);
        pedidoFalso.setDireccionEnvio("Calle Falsa 123");
    }

    @Test
    @DisplayName("Debe obtener todos los despachos")
    void obtenerTodosExito() {
        when(repository.findAll()).thenReturn(Arrays.asList(guiaFalsa));
        List<guia_despachoResponseDTO> resultado = service.obtenerTodos();
        assertFalse(resultado.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe guardar un despacho usando Feign exitosamente")
    void guardarConFeignExito() {
        // ARRANGE: Fingimos que no existe el despacho previo
        when(repository.findByIdPedido(100L)).thenReturn(Optional.empty());
        // Fingimos que el cliente de Pedidos responde con éxito
        when(pedidoClient.obtenerPedidoPorId(100L)).thenReturn(pedidoFalso);
        // Fingimos que se guarda en BD
        when(repository.save(any(guia_despacho.class))).thenReturn(guiaFalsa);

        // ACT
        guia_despachoResponseDTO resultado = service.guardar(requestDTO);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("PREPARANDO", resultado.getEstadoEnvio());
        verify(pedidoClient, times(1)).obtenerPedidoPorId(100L);
        verify(notificacionClient, times(1)).enviarNotificacion(any(NotificacionRequestDTO.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar si el pedido ya tiene despacho")
    void guardarLanzaExcepcionSiYaExiste() {
        when(repository.findByIdPedido(100L)).thenReturn(Optional.of(guiaFalsa));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.guardar(requestDTO);
        });

        assertTrue(exception.getMessage().contains("Ya existe una guía de despacho"));
    }

    @Test
    @DisplayName("Debe buscar despacho por ID de pedido")
    void buscarPorIdPedidoExito() {
        when(repository.findByIdPedido(100L)).thenReturn(Optional.of(guiaFalsa));
        Optional<guia_despachoResponseDTO> resultado = service.buscarPorIdPedido(100L);
        assertTrue(resultado.isPresent());
        verify(repository, times(1)).findByIdPedido(100L);
    }
}