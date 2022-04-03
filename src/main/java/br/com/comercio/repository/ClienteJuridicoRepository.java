package br.com.comercio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.comercio.model.ClienteJuridico;

@Repository
@Transactional
public interface ClienteJuridicoRepository extends JpaRepository<ClienteJuridico, Long> {
	ClienteJuridico findByCnpj(String cnpj);

	@Query(value = "SELECT * FROM cliente_juridico AS c WHERE c.usuario_email = :email", nativeQuery = true)
	ClienteJuridico findClienteByEmail(@Param("email") String email);

}
