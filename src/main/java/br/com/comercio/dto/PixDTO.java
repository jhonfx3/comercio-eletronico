package br.com.comercio.dto;

public class PixDTO {
	private String nome;
	private String sobrenome;
	private String email;
	private String tipoIdentificacao;
	private String numeroIdentificacao;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTipoIdentificacao() {
		return tipoIdentificacao;
	}
	public void setTipoIdentificacao(String tipoIdentificacao) {
		this.tipoIdentificacao = tipoIdentificacao;
	}
	public String getNumeroIdentificacao() {
		return numeroIdentificacao;
	}
	public void setNumeroIdentificacao(String numeroIdentificacao) {
		this.numeroIdentificacao = numeroIdentificacao;
	}
}
