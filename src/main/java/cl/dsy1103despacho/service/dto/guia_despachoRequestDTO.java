package cl.dsy1103despacho.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class guia_despachoRequestDTO {

    @NotNull(message = "El ID del pedido no puede ser nulo")
    @Positive(message = "El ID del pedido debe ser mayor a cero")
    private Long idPedido;

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String direccionEnvio;
}
