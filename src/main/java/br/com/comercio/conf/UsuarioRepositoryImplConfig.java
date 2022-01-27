package br.com.comercio.conf;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.comercio.model.Usuario;
import br.com.comercio.model.UsuarioRepositoryImpl;
import br.com.comercio.repository.UsuarioRepository;

@Configuration
public class UsuarioRepositoryImplConfig {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UsuarioRepository repository;

	@Bean
	public UsuarioRepositoryImpl retorna() {
		return new UsuarioRepositoryImpl(Usuario.class, em, repository);
	}
}
