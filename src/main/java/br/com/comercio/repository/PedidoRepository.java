package br.com.comercio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.comercio.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, String> {
	@Query("SELECT p from Pedido p WHERE p.usuario.email = :usuarioEmail")
	List<Pedido> findPedidosByUsuario(String usuarioEmail);
	@Query(value = "SELECT * FROM Pedido p ORDER BY p.id LIMIT 1",nativeQuery = true)
	Pedido findFirst();
}
