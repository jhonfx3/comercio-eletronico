package br.com.comercio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.comercio.model.Authorities;
import br.com.comercio.model.Usuario;

@Repository
@Transactional
public interface AuthoritiesRepository extends JpaRepository<Authorities, String> {

	@Modifying
	@Query("UPDATE Authorities a SET a.usuarios = :usuarios WHERE a.authority = :authority")
	void atualizaUsuario(String authority, List<Usuario> usuarios);

}
