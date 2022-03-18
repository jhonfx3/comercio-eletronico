package br.com.comercio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.comercio.model.Cliente;

@Repository
@Transactional
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
