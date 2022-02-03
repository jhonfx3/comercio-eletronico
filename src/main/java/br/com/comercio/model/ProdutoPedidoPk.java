package br.com.comercio.model;

import java.io.Serializable;
import java.util.Objects;

public class ProdutoPedidoPk implements Serializable{
	private static final long serialVersionUID = 1L;
	private Produto produto;
	private Pedido pedido;
	@Override
	public int hashCode() {
		return Objects.hash(pedido, produto);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProdutoPedidoPk other = (ProdutoPedidoPk) obj;
		return Objects.equals(pedido, other.pedido) && Objects.equals(produto, other.produto);
	}
	
	
}
