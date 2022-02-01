package br.com.comercio.impl;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.comercio.model.Usuario;
import br.com.comercio.repository.UsuarioRepository;

@Transactional
public class UsuarioRepositoryImpl extends SimpleJpaRepository<Usuario, String> {
	// @PersistenceContext
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

	public void updateUser(Usuario usuario) {
		System.out.println(usuario.getCpf());
		em.merge(usuario);
		System.out.println("atualizei o usuario...");
	}

	public void teste(String emailAtualizar, String role) {
		em.createQuery("UPDATE usuario_roles u set u.usuario_email = :emailAtualizar WHERE u.roles_authority = :role")
				.setParameter("emailAtualizar", emailAtualizar).setParameter("role", role).executeUpdate();
	}

}
