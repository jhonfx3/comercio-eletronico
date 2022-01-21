package br.com.comercio.model;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.comercio.enums.TipoPreco;

public class CarrinhoItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Produto produto;

	public CarrinhoItem(Produto produto) {
		this.produto = produto;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public BigDecimal getTotalCarrinhoItem(int quantidade, TipoPreco tipo) {
		return this.getProduto().precoPara(tipo).multiply(new BigDecimal(quantidade));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((produto == null) ? 0 : produto.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarrinhoItem other = (CarrinhoItem) obj;
		if (produto == null) {
			if (other.produto != null)
				return false;
		} else if (!produto.equals(other.produto))
			return false;
		return true;
	}

}
