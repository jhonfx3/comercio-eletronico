package br.com.comercio.model;

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
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.comercio.hibernategroups.EditarUsuario;
import br.com.comercio.hibernategroups.PersistirUsuario;
import br.com.comercio.interfaces.SenhaFraca;
import br.com.comercio.interfaces.UniqueUsernameEditar;
import br.com.comercio.interfaces.UniqueUsernamePersistir;

@Entity
@DynamicUpdate
@DynamicInsert
public class Usuario implements UserDetails {
	private static final long serialVersionUID = 1L;
	@Id
	@NotEmpty(message = "E-mail é obrigatório")
	@UniqueUsernameEditar(groups = EditarUsuario.class)
	@UniqueUsernamePersistir(groups = PersistirUsuario.class)
	@Email(message = "E-mail inválido")

	private String email;

	@NotEmpty(message = "Senha é obrigatória")
	@Length(min = 4, max = 10, groups = PersistirUsuario.class)
	@SenhaFraca
	private String password;
	private Boolean enabled;

	@Column(name = "codigo_verificacao", unique = true)
	private String codigoVerificacao;

	@OneToOne(mappedBy = "usuario")
	private Cliente cliente;

	public String getCodigoVerificacao() {
		return codigoVerificacao;
	}

	public void setCodigoVerificacao(String codigoVerificacao) {
		this.codigoVerificacao = codigoVerificacao;
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

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

}
