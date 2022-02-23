package br.com.comercio.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.comercio.hibernategroups.EditarUsuario;
import br.com.comercio.hibernategroups.PersistirUsuario;
import br.com.comercio.impl.UsuarioRepositoryImpl;
import br.com.comercio.interfaces.AntiXss;
import br.com.comercio.interfaces.DataFormatValidacao;
import br.com.comercio.interfaces.SenhaFraca;
import br.com.comercio.interfaces.UniqueColumn;
import br.com.comercio.interfaces.UniqueColumnEditar;
import br.com.comercio.interfaces.UniqueUsernameEditar;
import br.com.comercio.interfaces.UniqueUsernamePersistir;
import br.com.comercio.service.CriptografiaService;

@Entity
@DynamicUpdate
@DynamicInsert
public class Usuario implements UserDetails {
	private static final long serialVersionUID = 1L;
	@Id
	@NotEmpty(message = "E-mail é obrigatório")
	@UniqueUsernameEditar(groups = EditarUsuario.class)
	@UniqueUsernamePersistir(groups = PersistirUsuario.class)
	private String email;
	@NotEmpty(message = "Nome é obrigatório")
	@AntiXss
	private String nome;
	@NotEmpty(message = "Sobrenome é obrigatório")
	@AntiXss
	private String sobrenome;

	@NotEmpty(message = "Senha é obrigatória")
	@Length(min = 4, max = 10, groups = PersistirUsuario.class)
	@SenhaFraca
	private String password;
	private Boolean enabled;

	@CPF(message = "CPF informado é inválido")
	@Column(unique = true)
	@UniqueColumnEditar(groups = EditarUsuario.class, campo = "Cpf", tipoParametro = String.class, classeASerValidada = Usuario.class, nomeClasseImplRepository = UsuarioRepositoryImpl.class, message = "CPF já existente")
	@UniqueColumn(groups = PersistirUsuario.class, campo = "Cpf", tipoParametro = String.class, classeASerValidada = Usuario.class, nomeClasseImplRepository = UsuarioRepositoryImpl.class, message = "CPF já existente")
	private String cpf;

	private String rg;
	private String telefone;
	@Column(name = "codigo_verificacao")
	private String codigoVerificacao;

	public String getCodigoVerificacao() {
		return codigoVerificacao;
	}

	public void setCodigoVerificacao(String codigoVerificacao) {
		this.codigoVerificacao = codigoVerificacao;
	}

	@javax.validation.constraints.NotNull(message = "A data de aniversário é obrigatória")
	@Past(message = "Data de nascimento deve estar no passado")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@DataFormatValidacao(message = "A data informada é inválida")
	private LocalDate nascimento;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSobrenome() {
		return sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}

	public LocalDate getNascimento() {
		return nascimento;
	}

	public void setNascimento(LocalDate nascimento) {
		this.nascimento = nascimento;
	}

	@OneToMany(mappedBy = "usuario")
	private List<Endereco> endereco;

	public String getCpf() {
		try {
			return new CriptografiaService().decriptar(cpf);
		} catch (Exception e) {
			return cpf;
		}
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(email, other.email);
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

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_email"), inverseJoinColumns = @JoinColumn(name = "roles_authority"))
	private List<Authorities> roles;

	@Override
	public String getUsername() {
		return this.email;
	}

	public List<Authorities> getRoles() {
		return roles;
	}

	public void setRoles(List<Authorities> roles) {
		this.roles = roles;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUsername(String username) {
		this.email = username;
	}

	public Usuario() {
		setEnabled(false);
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.roles = (List<Authorities>) authorities;
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
		return this.roles;
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
