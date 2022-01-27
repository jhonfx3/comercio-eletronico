package br.com.comercio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.comercio.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

	@Query("SELECT u from Usuario u WHERE u.username = :username")
	Usuario findByIdUser(@Param("username") String username);

	Usuario findByCpf(String cpf);

	Usuario findByUsername(String username);

}
