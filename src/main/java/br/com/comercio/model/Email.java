package br.com.comercio.model;

import br.com.comercio.enums.StatusEmail;

public class Email {
	private String origem;
	private String destinatario;
	private String assunto;
	private String mensagem;
	private StatusEmail status;
	
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public String getDestinatario() {
		return destinatario;
	}
	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}
	public String getAssunto() {
		return assunto;
	}
	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public StatusEmail getStatus() {
		return status;
	}
	public void setStatus(StatusEmail status) {
		this.status = status;
	}
	
	
	
	
	
}
