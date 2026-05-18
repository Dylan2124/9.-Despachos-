package cl.dsy1103despacho.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequestDTO {
    private Long idUsuario;
    private Long idPedido;
    private String tipo;
    private String mensaje;

}
