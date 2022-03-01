package br.com.comercio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.comercio.model.Categoria;
import br.com.comercio.model.Produto;
import br.com.comercio.repository.CategoriaRepository;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@GetMapping("/{nome}")
	public String produtosPorCategoria(@PathVariable("nome") String nome, Model model) {
		Categoria categoria = categoriaRepository.findByNome(nome);
		List<Produto> produtos = categoria.getProdutos();
		model.addAttribute("produtos", produtos);
		model.addAttribute("categorias", categoriaRepository.findAll());
		return "home";
	}

}
