package br.com.comercio.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mercadopago.exceptions.MPException;

import br.com.comercio.model.Categoria;
import br.com.comercio.model.Produto;
import br.com.comercio.repository.CategoriaRepository;
import br.com.comercio.repository.ProdutoRepository;

@Controller
public class HomeController {

	@Autowired
	private ProdutoRepository produtoRepository;
	@Autowired
	private CategoriaRepository categoriaRepository;

	@GetMapping("/")
	public String home(Model model, HttpServletRequest request) throws MPException {
		List<Produto> produtos = produtoRepository.findAll();
		model.addAttribute("produtos", produtos);
		List<Categoria> categorias = categoriaRepository.findAll();
		model.addAttribute("categorias", categorias);
		System.out.println(request.getRemoteAddr());
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
					"Desculpe, não foram encontrados resultados para sua pesquisa: '" + pesquisar + "'");
		}
		return "home";
	}

	@GetMapping("/desenvolvedor")
	public String sobreODesenvolvedor(Model model) throws MPException {
		return "desenvolvedor";
	}
}
