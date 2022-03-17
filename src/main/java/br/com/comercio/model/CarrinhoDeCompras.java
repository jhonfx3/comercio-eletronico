package br.com.comercio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CarrinhoDeCompras implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<CarrinhoItem, Integer> itens = new LinkedHashMap<>();

	public void adiciona(CarrinhoItem item) {
		if (!itens.containsKey(item)) {
			itens.put(item, 1);
		}
	}

	public void adiciona(CarrinhoItem item, Integer quantidade) {
		itens.put(item, quantidade);
	}

	public void remove(Produto produto) {
		itens.remove(new CarrinhoItem(produto));
	}

	public int getQuantidade(CarrinhoItem item) {
		if (!itens.containsKey(item)) {
			itens.put(item, 0);
		}
		return itens.get(item);
	}

	public BigDecimal getTotalCarrinho() {
		BigDecimal total = BigDecimal.ZERO;

		for (CarrinhoItem item : itens.keySet()) {
			total = total.add(item.getTotalCarrinhoItem(getQuantidade(item)));
		}
		return total;
	}

	public Collection<CarrinhoItem> getItens() {
		return itens.keySet();
	}

	public Map<CarrinhoItem, Integer> getItensMap() {
		return this.itens;
	}

	public void limpaCarrinho() {
		this.itens.clear();
	}

	public float calculaValorAPrazo(float valorDaCompra, Integer quantidadeParcelas) {
		float porcentagemDeAcrescimo = 0;

		switch (quantidadeParcelas) {
		case 2:
			porcentagemDeAcrescimo = (float) 6.76;
			break;
		case 3:
			porcentagemDeAcrescimo = (float) 8.44;
			break;
		case 4:
			porcentagemDeAcrescimo = (float) 10.23;
			break;
		case 5:
			porcentagemDeAcrescimo = (float) 11.93;
			break;
		case 6:
			porcentagemDeAcrescimo = (float) 13.58;
			break;
		case 7:
			porcentagemDeAcrescimo = (float) 15.01;
			break;
		case 8:
			porcentagemDeAcrescimo = (float) 16.90;
			break;
		case 9:
			porcentagemDeAcrescimo = (float) 18.86;
			break;
		case 10:
			porcentagemDeAcrescimo = (float) 20.07;
			break;
		case 11:
			porcentagemDeAcrescimo = (float) 21.92;
			break;
		case 12:
			porcentagemDeAcrescimo = (float) 23.75;
			break;
		}
		float valor;
		valor = (valorDaCompra * porcentagemDeAcrescimo) / 100;
		valorDaCompra += valor;
		BigDecimal setScale = new BigDecimal(valorDaCompra).setScale(2, RoundingMode.HALF_UP);
		System.out.println(setScale);
		return new BigDecimal(valorDaCompra).setScale(2, RoundingMode.HALF_UP).floatValue();
	}

}
