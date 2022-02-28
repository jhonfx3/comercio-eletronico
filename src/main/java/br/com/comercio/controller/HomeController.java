package br.com.comercio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mercadopago.exceptions.MPException;

import br.com.comercio.model.Produto;
import br.com.comercio.repository.ProdutoRepository;

@Controller
public class HomeController {

	@Autowired
	private ProdutoRepository produtoRepository;

	@GetMapping("/")
	public String home(Model model) throws MPException {
		List<Produto> produtos = produtoRepository.findAll();
		model.addAttribute("produtos", produtos);
		return "home";
	}

	@GetMapping("/busca")
	public String buscaProduto(@Param(value = "pesquisa") String pesquisar, Model model) {
		// Limpando caracteres de abrir e fechar <>
		pesquisar = pesquisar.replace("<", "");
		pesquisar = pesquisar.replace(">", "");
		List<Produto> produtos = null;
		try {
			produtos = produtoRepository.findProdutoByPesquisa(pesquisar);
			model.addAttribute("produtos", produtos);
			if (produtos.size() > 0) {
				model.addAttribute("sucessoPesquisa", "Resultados para: " + pesquisar);
			}
		} catch (Exception e) {
		}
		if (produtos == null || produtos.isEmpty()) {
			model.addAttribute("erroPesquisa",
					"Desculpe, não foi encontrado nenhum resultado para sua pesquisa: '" + pesquisar + "'");
		}
		return "home";
	}

	@GetMapping("/desenvolvedor")
	public String sobreODesenvolvedor(Model model) throws MPException {
		return "desenvolvedor";
	}
}
