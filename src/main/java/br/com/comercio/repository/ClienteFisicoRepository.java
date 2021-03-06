package br.com.comercio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.comercio.model.ClienteFisico;

@Repository
@Transactional
public interface ClienteFisicoRepository extends JpaRepository<ClienteFisico, Long> {
	ClienteFisico findByCpf(String cpf);

	@Query(value = "SELECT * FROM cliente_fisico AS c WHERE c.usuario_email = :email", nativeQuery = true)
	ClienteFisico findClienteByEmail(@Param("email") String email);

}
