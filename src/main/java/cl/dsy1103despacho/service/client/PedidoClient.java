package cl.dsy1103despacho.service.client;

import cl.dsy1103despacho.service.dto.PedidoExternoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pedidos", url = "http://localhost:8086/api/pedidos")
public interface PedidoClient {

    @GetMapping("/{id}")
    PedidoExternoDTO obtenerPedidoPorId(@PathVariable("id") Long id);
}