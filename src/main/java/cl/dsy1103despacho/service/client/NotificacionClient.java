package cl.dsy1103despacho.service.client;

import cl.dsy1103despacho.service.dto.NotificacionRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-notificaciones", url = "http://localhost:8090/api/notificaciones")
public interface NotificacionClient {

    @PostMapping
    void enviarNotificacion(@RequestBody NotificacionRequestDTO request);
}