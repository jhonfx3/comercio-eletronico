package br.com.comercio.impl;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.comercio.model.Cliente;
import br.com.comercio.model.ClienteFisico;
import br.com.comercio.repository.ClienteFisicoRepository;

@Transactional
public class ClienteRepositoryImpl extends SimpleJpaRepository<ClienteFisico, Long> {
	// @PersistenceContext
	private EntityManager em;
	@Autowired
	private ClienteFisicoRepository repository;

	public ClienteRepositoryImpl(Class<ClienteFisico> domainClass, EntityManager em,
			ClienteFisicoRepository repository) {
		super(domainClass, em);
		this.em = em;
		this.repository = repository;
	}

	public void updateCliente(Cliente cliente) {
		em.merge(cliente);
	}

	public ClienteFisico findByCpf(String cpf) {
		return repository.findByCpf(cpf);
	}

}
