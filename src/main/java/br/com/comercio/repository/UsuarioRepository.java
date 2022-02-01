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
	Usuario findByCpf(String cpf);

	Usuario findByEmail(String email);

	@Modifying
	@Query("UPDATE Usuario u set u.email = :emailNovo WHERE u.email = :emailAtual")
	void atualizaEmail(String emailNovo, String emailAtual);

	@Modifying
	@Query("DELETE from Usuario u WHERE u.email = :email")
	void deletaUsuario(String email);
}
