package cl.dsy1103despacho.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "guia_despacho")
public class guia_despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id_guia")
    private Long idGuia;

    @Column(name = "id_pedido")
    private Long idPedido;

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    @Column(name = "estado_envio")
    private String estadoEnvio;

    @Column(name = "fecha_estimada")
    private LocalDateTime fechaEstimada;


}
