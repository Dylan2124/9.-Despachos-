package cl.dsy1103despacho.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class guia_despachoResponseDTO {

    private Long idGuia;
    private Long idPedido;
    private String direccionEnvio;
    private String estadoEnvio;
    private LocalDateTime fechaEstimada;
}