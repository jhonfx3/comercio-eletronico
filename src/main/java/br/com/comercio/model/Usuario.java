package br.com.comercio.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.comercio.impl.UsuarioRepositoryImpl;
import br.com.comercio.interfaces.DataFormatValidacao;
import br.com.comercio.interfaces.UniqueColumn;
import br.com.comercio.interfaces.UniqueUsername;

@Entity
public class Usuario implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@UniqueUsername
	@NotEmpty(message = "Usuário é obrigatório")
	private String username;
	@NotEmpty(message = "Senha é obrigatória")
	private String password;
	private Boolean enabled;
	@CPF(message = "CPF informado é inválido")
	@Column(unique = true)
	@UniqueColumn(campo = "Cpf", tipoParametro = String.class, classeASerValidada = Usuario.class, nomeClasseImplRepository = UsuarioRepositoryImpl.class, message = "CPF já existente")
	private String cpf;
	private String rg;
	private String telefone;

	@javax.validation.constraints.NotNull(message = "A data de aniversário é obrigatória")
	@Past(message = "Data de nascimento deve estar no passado")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@DataFormatValidacao(message = "A data informada é inválida")
	private LocalDate nascimento;

	public LocalDate getNascimento() {
		return nascimento;
	}

	public void setNascimento(LocalDate nascimento) {
		this.nascimento = nascimento;
	}

	@OneToMany(mappedBy = "usuario")
	private List<Endereco> endereco;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public LocalDate getAniversario() {
		return nascimento;
	}

	public void setAniversario(LocalDate aniversario) {
		this.nascimento = aniversario;
	}

	public List<Endereco> getEndereco() {
		return endereco;
	}

	public void setEndereco(List<Endereco> endereco) {
		this.endereco = endereco;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	private List<Authorities> authorities;

	@Override
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Usuario() {
		setEnabled(true);
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setAuthorities(List<Authorities> authorities) {
		this.authorities = authorities;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return this.authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return this.enabled;
	}

}
