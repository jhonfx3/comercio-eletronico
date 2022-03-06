package br.com.comercio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.comercio.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	@Query("SELECT p from Produto p WHERE p.id = :id")
	Produto findByIdProduto(@Param("id") Long id);

	@Query("SELECT p from Produto p WHERE lower(p.nome) LIKE %:pesquisa% OR lower(p.descricao) LIKE %:pesquisa%")
	List<Produto> findProdutoByPesquisa(@Param("pesquisa") String pesquisa);

}
