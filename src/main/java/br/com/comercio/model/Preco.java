package br.com.comercio.model;

import java.math.BigDecimal;

import javax.persistence.Embeddable;

import br.com.comercio.enums.TipoPreco;

@Embeddable
public class Preco {
	private TipoPreco tipo;
	private BigDecimal valor;
	public TipoPreco getTipo() {
		return tipo;
	}
	public void setTipo(TipoPreco tipo) {
		this.tipo = tipo;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
}
