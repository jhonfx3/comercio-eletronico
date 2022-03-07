package br.com.comercio.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	@GetMapping("/formulario/cadastrar")
	public String formulario(Categoria categoria) {
		return "categoria/formulario";
	}

	@PostMapping("/novo")
	public String novo(@Valid Categoria categoria, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return "categoria/formulario";
		}
		categoriaRepository.save(categoria);
		attributes.addFlashAttribute("sucesso", "Categoria " + categoria.getNome() + " cadastrada com sucesso");
		return "redirect:/categoria/formulario/cadastrar";
	}
}
