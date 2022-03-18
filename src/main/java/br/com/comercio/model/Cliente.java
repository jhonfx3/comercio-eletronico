package br.com.comercio.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;

import br.com.comercio.interfaces.AntiXss;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotEmpty(message = "Nome é obrigatório")
	@AntiXss
	private String nome;
	private String telefone;
	@OneToMany(mappedBy = "cliente")
	private List<Endereco> endereco;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

}
