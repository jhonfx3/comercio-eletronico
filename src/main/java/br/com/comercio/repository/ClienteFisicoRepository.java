package br.com.comercio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.comercio.model.Cliente;
import br.com.comercio.model.ClienteFisico;

@Repository
@Transactional
public interface ClienteFisicoRepository extends JpaRepository<ClienteFisico, Long> {
	ClienteFisico findByCpf(String cpf);


}
