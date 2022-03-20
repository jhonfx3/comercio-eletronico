package br.com.comercio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.comercio.model.Usuario;

@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

	Usuario findByEmail(String email);

	Usuario findByCodigoVerificacao(String codigo);

	@Query("SELECT u from Usuario u WHERE u.email = :email AND u.password = :password")
	Usuario findByEmailESenha(String email, String password);

	@Modifying
	@Query("UPDATE Usuario u set u.email = :emailNovo WHERE u.email = :emailAtual")
	void atualizaEmail(String emailNovo, String emailAtual);

	@Modifying
	@Query("UPDATE Usuario u set u.password = :password WHERE u.email = :email")
	void atualizaSenha(String email, String password);

	@Modifying
	@Query("DELETE from Usuario u WHERE u.email = :email")
	void deletaUsuario(String email);
}
