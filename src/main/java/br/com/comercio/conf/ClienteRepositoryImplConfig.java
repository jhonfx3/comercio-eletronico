package br.com.comercio.conf;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.comercio.impl.ClienteRepositoryImpl;
import br.com.comercio.model.ClienteFisico;
import br.com.comercio.repository.ClienteFisicoRepository;

@Configuration
public class ClienteRepositoryImplConfig {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ClienteFisicoRepository repository;

	@Bean
	public ClienteRepositoryImpl retorna() {
		return new ClienteRepositoryImpl(ClienteFisico.class, em, repository);
	}
}
