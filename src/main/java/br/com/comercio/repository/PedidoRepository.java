package br.com.comercio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.comercio.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {


}
