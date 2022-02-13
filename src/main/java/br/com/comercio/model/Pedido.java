package br.com.comercio.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import br.com.comercio.enums.StatusPedido;

@Entity
public class Pedido {
	@Id
	private String id;
	private BigDecimal total;

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.MERGE)
	private List<ProdutoPedido> produtos;

	@ManyToOne
	private Usuario usuario;
	@Enumerated(EnumType.STRING)
	private StatusPedido status;
	@Column(name = "metodo_pagamento")
	private String metodoPagamento;
	private Integer parcelas;
	@Column(name = "valor_parcela")
	private BigDecimal valorParcela;
	@Column(name = "data_criacao")
	private LocalDate data;
	@Column(columnDefinition = "VARCHAR(8000)")
	private String qrCodeBase64;
	@Column(columnDefinition = "VARCHAR(2048)", name = "link_boleto")
	private String linkBoleto;

	public LocalDate getData() {
		return data;
	}

	public String getQrCodeBase64() {
		return qrCodeBase64;
	}

	public String getLinkBoleto() {
		return linkBoleto;
	}

	public String getMetodoPagamento() {
		return metodoPagamento;
	}

	public void setMetodoPagamento(String metodoPagamento) {
		this.metodoPagamento = metodoPagamento;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public String getId() {
		return id;
	}

	public Integer getParcelas() {
		return parcelas;
	}

	public void setParcelas(Integer parcelas) {
		this.parcelas = parcelas;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public StatusPedido getStatus() {
		return status;
	}

	public void setStatus(StatusPedido status) {
		this.status = status;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public List<ProdutoPedido> getProdutos() {
		return produtos;
	}

	public void setProdutos(List<ProdutoPedido> produtos) {
		this.produtos = produtos;
	}

	public void setQrCodeBase64(String qrCodeBase64) {
		this.qrCodeBase64 = qrCodeBase64;

	}

	public void setLinkBoleto(String linkBoleto) {
		this.linkBoleto = linkBoleto;
		// TODO Auto-generated method stub

	}

}
