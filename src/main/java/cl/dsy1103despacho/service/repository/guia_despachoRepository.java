package cl.dsy1103despacho.service.repository;

import cl.dsy1103despacho.service.model.guia_despacho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface guia_despachoRepository extends JpaRepository<guia_despacho, Long> {

    // Buscar un despacho usando el ID del pedido
    Optional<guia_despacho> findByIdPedido(Long idPedido);
}