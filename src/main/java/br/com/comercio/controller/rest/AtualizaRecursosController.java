package br.com.comercio.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.comercio.enums.TipoPreco;
import br.com.comercio.model.CarrinhoDeCompras;
import br.com.comercio.model.CarrinhoItem;
import br.com.comercio.model.Produto;
import br.com.comercio.repository.ProdutoRepository;

@RestController
@RequestMapping(value = "atualiza")
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class AtualizaRecursosController {

	@Autowired
	private CarrinhoDeCompras carrinho;

	@Autowired
	private ProdutoRepository produtoRepository;

	@PostMapping("/carrinho")
	public ResponseEntity<String> atualizaQuantidadeCarrinho(@RequestBody String json) {
		JsonObject obj = new Gson().fromJson(json, JsonObject.class);
		String produtoId = obj.get("produtoId").getAsString();
		String quantidade = obj.get("quantidade").getAsString();
		System.out.println("produtoId: " + produtoId + " Quantidade: " + quantidade);
		Produto produto = produtoRepository.findById(Long.valueOf(produtoId)).get();
		carrinho.adiciona(new CarrinhoItem(produto), Integer.valueOf(quantidade));
		String totalVista = String.valueOf(carrinho.getTotalCarrinho(TipoPreco.VISTA));
		String totalPrazo = String.valueOf(carrinho.getTotalCarrinho(TipoPreco.PRAZO));
		JsonObject resposta = new JsonObject();
		resposta.addProperty("totalVista", totalVista);
		resposta.addProperty("totalPrazo", totalPrazo);
		System.out.println(resposta.toString());
		return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(resposta.toString()));
	}

}
