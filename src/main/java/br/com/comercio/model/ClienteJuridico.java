package br.com.comercio.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.br.CNPJ;

import br.com.comercio.service.CriptografiaService;

@Entity
public class ClienteJuridico extends Cliente {

	@NotEmpty
	@CNPJ(message = "CNPJ inválido")
	private String cnpj;
	private String ie;
	private LocalDate fundacao;
	private String site;

	public String getCnpj() {
		try {
			return new CriptografiaService().decriptar(this.cnpj);
		} catch (Exception e) {
			return this.cnpj;
		}
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getIe() {
		return ie;
	}

	public void setIe(String ie) {
		this.ie = ie;
	}

	public LocalDate getFundacao() {
		return fundacao;
	}

	public void setFundacao(LocalDate fundacao) {
		this.fundacao = fundacao;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

}
