package br.com.comercio.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.groups.ConvertGroup;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;

import br.com.comercio.hibernategroups.EditarUsuario;
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

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@Valid
	//@ConvertGroup(from = Default.class, to = EditarUsuario.class)
	private Usuario usuario;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Endereco> getEndereco() {
		return endereco;
	}

	public void setEndereco(List<Endereco> endereco) {
		this.endereco = endereco;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

}
