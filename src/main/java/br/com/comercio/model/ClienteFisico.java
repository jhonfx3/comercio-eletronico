package br.com.comercio.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import br.com.comercio.hibernategroups.PersistirUsuario;
import br.com.comercio.impl.ClienteRepositoryImpl;
import br.com.comercio.interfaces.AntiXss;
import br.com.comercio.interfaces.DataFormatValidacao;
import br.com.comercio.interfaces.Maioridade;
import br.com.comercio.interfaces.UniqueColumn;
import br.com.comercio.service.CriptografiaService;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class ClienteFisico extends Cliente {

	@NotEmpty(message = "cpf obrigatório")
	@CPF(message = "CPF informado é inválido")
	@Column(unique = true)
	@UniqueColumn(campo = "Cpf", classeASerValidada = ClienteFisico.class, tipoParametro = String.class, nomeClasseImplRepository = ClienteRepositoryImpl.class, groups = PersistirUsuario.class, message = "cpf já existente")
	private String cpf;

	private String rg;
	@NotNull(message = "A data de aniversário é obrigatória")
	@Past(message = "Data de nascimento deve estar no passado")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@DataFormatValidacao(message = "A data informada é inválida")
	@Maioridade
	private LocalDate nascimento;

	@NotEmpty(message = "sobrenome obrigatório")
	@AntiXss
	private String sobrenome;

	public String getCpf() {
		try {
			return new CriptografiaService().decriptar(this.cpf);
		} catch (Exception e) {
			return this.cpf;
		}
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

	public LocalDate getNascimento() {
		return nascimento;
	}

	public void setNascimento(LocalDate nascimento) {
		this.nascimento = nascimento;
	}

	public String getSobrenome() {
		return sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}

}
