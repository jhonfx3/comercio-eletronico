package br.com.comercio.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import br.com.comercio.model.Usuario;
import br.com.comercio.repository.UsuarioRepository;

public class UsuarioRepositoryImpl extends SimpleJpaRepository<Usuario, String> {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UsuarioRepository repository;

	public UsuarioRepositoryImpl(Class<Usuario> domainClass, EntityManager em, UsuarioRepository repository) {
		super(domainClass, em);
		this.em = em;
		this.repository = repository;
	}

	public Usuario findByCpf(String cpf) {
		return (Usuario) repository.findByCpf(cpf);
	}

	public Usuario findByUsername(String username) {
		return (Usuario) repository.findByUsername(username);
	}

}
